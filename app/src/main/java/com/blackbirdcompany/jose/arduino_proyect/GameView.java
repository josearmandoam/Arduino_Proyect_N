package com.blackbirdcompany.jose.arduino_proyect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 09/02/2017.
 */

public class GameView extends SurfaceView {

    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    private int RADIO, EJEX, EJEY;
    boolean db;
    ArrayList posiciones;
    private static final int RASTRO = 20; //numero de lineas que habrá de rastro

    public GameView(final Context context) {
        super(context);
        gameLoopThread = new GameLoopThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
        posiciones=new ArrayList();
        ang = 0;
        sumaAng = -1;
        bolAux=true;
        objetoDtectado(60);
        objetoDtectado(120);

        lineas = new float[RASTRO][2];
        for(int i = 0; i<RASTRO; i++) {
            for (int n = 0; n<2; n++){
                lineas[i][n] = 0;
            }
        }
        degra = new int[RASTRO];
        int aux = 250;
        int resta = aux/RASTRO;
        for(int i = 0; i<degra.length; i++) {
            degra[i] = aux;
            aux -= resta;
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onDraw(Canvas canvas) {

        //Define el radio de la circunferencia exterior
        RADIO = getWidth()/2 - 30;
        EJEX = getWidth()/2;
        EJEY = getHeight();

        //Vuelve a dibujar la base del canvas
        Paint pincel=new Paint();
        basePrincipal(canvas,pincel);

        pincel.setColor(Color.GREEN);
        //Dibuja las lineas del radar
        pincel.setStrokeWidth(8);

        dibujaLinea(canvas, pincel, ang);

        if(bolAux) {
            ang++;
            sumaAng=-1;
        }else {
            ang--;
            sumaAng=1;
        }
        if(ang>=180 || ang<=0)
            bolAux=!bolAux;

    }

    int ang, sumaAng;
    boolean bolAux;

    private void object(Canvas canvas,Paint pincel,float x, float y){
        pincel.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x,y,50,pincel);
    }

    private void basePrincipal(Canvas canvas,Paint pincel){

        //Dibuja fondo
        pincel.setColor(Color.BLACK);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), pincel);

        //Prepara el pincel
        pincel.setStrokeWidth(5);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setColor(Color.GREEN);

        //Dibuja las 5 lineas de los grados (30, 60, 90, 120, 150)
        canvas.drawLine(EJEX, EJEY, (float)((getWidth()/2)-(Math.cos(Math.toRadians(30))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(30))*(50 + RADIO))),pincel);
        canvas.drawLine(EJEX, EJEY, (float)((getWidth()/2)-(Math.cos(Math.toRadians(60))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(60))*(50 + RADIO))),pincel);
        canvas.drawLine(EJEX, EJEY, (float)((getWidth()/2)-(Math.cos(Math.toRadians(90))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(90))*(50 + RADIO))),pincel);
        canvas.drawLine(EJEX, EJEY, (float)((getWidth()/2)-(Math.cos(Math.toRadians(120))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(120))*(50 + RADIO))),pincel);
        canvas.drawLine(EJEX, EJEY, (float)((getWidth()/2)-(Math.cos(Math.toRadians(150))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(150))*(50 + RADIO))),pincel);

        //Dibuja linea inferior
        canvas.drawLine(0,getHeight()-3,getWidth(),getHeight()-3,pincel);

        //Dibuja los circulos
        pincel.setStrokeWidth(5);
        canvas.drawCircle(EJEX,EJEY,RADIO-600,pincel);
        canvas.drawCircle(EJEX,EJEY,RADIO-400,pincel);
        canvas.drawCircle(EJEX,EJEY,RADIO-200,pincel);
        canvas.drawCircle(EJEX,EJEY,RADIO,pincel);

        //Dibuja los numeros alrrededor de la circunferencia
        Path trazado = new Path();
        trazado.addCircle(EJEX, EJEY, 70 + RADIO, Path.Direction.CW);
        pincel.setStrokeWidth(5);
        pincel.setStyle(Paint.Style.FILL);
        pincel.setTextSize(40);
        pincel.setTypeface(Typeface.SANS_SERIF);
        float aux = (float)(2*Math.PI*(70 + RADIO))/360; //longitud de la circunferencia (2*PI*RADIO) por cada grado
        canvas.drawTextOnPath("30º", trazado, (330-1)*aux,0, pincel);
        canvas.drawTextOnPath("60º", trazado, (300-1)*aux,0, pincel);
        canvas.drawTextOnPath("90º", trazado, (270-1)*aux,0, pincel);
        canvas.drawTextOnPath("120º", trazado, (240-2)*aux,0, pincel);
        canvas.drawTextOnPath("150º", trazado, (210-2)*aux,0, pincel);
        if(posiciones.size()>0) {
            for (int i = 0; i < posiciones.size(); i++) {
                if (ang == Float.parseFloat(posiciones.get(i).toString())) {
                    pincel.setColor(Color.RED);
                    pincel.setStrokeWidth(10);
                    dibujaObjeto(canvas, pincel, Float.parseFloat(posiciones.get(i).toString()), (float) ((getWidth() / 2) - (Math.cos(Math.toRadians(Float.parseFloat(posiciones.get(i).toString()))) * (RADIO))),
                            (float) ((getHeight()) - (Math.sin(Math.toRadians(Float.parseFloat(posiciones.get(i).toString()))) * (RADIO))));
                    posiciones.remove(i);
                }
            }
        }
    }

    private void dibujaLinea(Canvas canvas, Paint pincel, int angulo){
        float x = (float)((getWidth()/2)-(Math.cos(Math.toRadians(angulo))*(50 + RADIO)));
        float y = (float)((getHeight())-(Math.sin(Math.toRadians(angulo))*(50 + RADIO)));
        pincel.setARGB(255, 255, 117, 20);
        canvas.drawLine(EJEX, EJEY, x, y, pincel);
        dibujaRastro(canvas, pincel);
        guardaLinea(x, y);
    }

    float lineas[][];
    int degra[];
    private void guardaLinea(float x, float y){
        //Mueve los objetos del array una posicion hacia el final y guarda la nueva linea en la posicion 0
        for(int i = RASTRO-1; i>0; i--) {
                lineas[i][0] = lineas[i-1][0];
                lineas[i][1] = lineas[i-1][1];
        }
        lineas[0][0]=x;
        lineas[0][1]=y;
    }

    private void dibujaRastro(Canvas canvas, Paint pincel){
        //Dibuja todas las lineas guardadas en el array de lineas
        for(int i = 0; i<RASTRO; i++) {
            if (lineas[i][0] != 0) {
                pincel.setARGB(degra[i], 255, 117, 20);
                canvas.drawLine(EJEX, EJEY, lineas[i][0], lineas[i][1], pincel);
            }
        }
    }
    private void dibujaObjeto(Canvas canvas,Paint pincel,float angulo,float x,float y){
        canvas.drawLine(x+10,y-100,x+10,y+100,pincel);
        canvas.drawLine(x+20,y-120,x+20,y+120,pincel);
        canvas.drawLine(x+30,y-140,x+30,y+140,pincel);
        canvas.drawLine(x+40,y-160,x+40,y+160,pincel);
        canvas.drawLine(x+50,y-140,x+50,y+140,pincel);
        canvas.drawLine(x+60,y-120,x+60,y+120,pincel);
        canvas.drawLine(x+70,y-100,x+70,y+100,pincel);
    }
    private void objetoDtectado(float angulo){
        posiciones.add(angulo);
    }
}
