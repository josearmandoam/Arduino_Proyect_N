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

import java.util.List;

/**
 * Created by jose on 09/02/2017.
 */

public class GameView extends SurfaceView {
    private Bitmap bmp;
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    private int RADIO;
    float cx,cy;
    private String TAG="AVISO";
    boolean ok=false;
    int incr=530,incr2=0;
    boolean posA,posB,posC,posD;

    public GameView(final Context context) {
        super(context);
        gameLoopThread = new GameLoopThread(this);
        holder = getHolder();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cx==v.getX() && cy==v.getY()){
                    Log.e(TAG,"has tocado la bola");
                }
            }
        });

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
                cx=getWidth();
                cy=getHeight()/2;
                posA=true;
                posB=false;
                posC=false;
                posD=false;

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
        //bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ang = 30;
        sumaAng = -1;
        bolAux=true;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onDraw(Canvas canvas) {

        //Define el radio de la circunferencia exterior
        RADIO = getWidth()/2 - 30;

        //Vuelve a dibujar la base del canvas
        Paint pincel=new Paint();
        basePrincipal(canvas,pincel);

        //Dibuja las lineas del radar
        pincel.setStrokeWidth(3);

        pincel.setARGB(255, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang))*(50 + RADIO))),pincel);
        pincel.setARGB(225, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*1)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*1)))*(50 + RADIO))),pincel);
        pincel.setARGB(200, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*2)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*2)))*(50 + RADIO))),pincel);
        pincel.setARGB(175, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*3)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*3)))*(50 + RADIO))),pincel);
        pincel.setARGB(150, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*4)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*4)))*(50 + RADIO))),pincel);
        pincel.setARGB(125, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*5)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*5)))*(50 + RADIO))),pincel);
        pincel.setARGB(100, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*6)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*6)))*(50 + RADIO))),pincel);
        pincel.setARGB(75, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*7)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*7)))*(50 + RADIO))),pincel);
        pincel.setARGB(50, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*8)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*8)))*(50 + RADIO))),pincel);
        pincel.setARGB(25, 255, 117, 20);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(ang+(sumaAng*9)))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(ang+(sumaAng*9)))*(50 + RADIO))),pincel);

        if(bolAux) {
            ang++;
            sumaAng=-1;
        }else {
            ang--;
            sumaAng=1;
        }
        if(ang>=150 || ang<=30)
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

        //ahora dibujo el circulo con las coordenadas
        pincel.setStrokeWidth(5);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setColor(Color.GREEN);

        //Dibuja las 5 lineas de los grados (30, 60, 90, 120, 150)
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(30))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(30))*(50 + RADIO))),pincel);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(60))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(60))*(50 + RADIO))),pincel);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(90))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(90))*(50 + RADIO))),pincel);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(120))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(120))*(50 + RADIO))),pincel);
        canvas.drawLine(getWidth()/2, getHeight(), (float)((getWidth()/2)-(Math.cos(Math.toRadians(150))*(50 + RADIO))), (float)((getHeight())-(Math.sin(Math.toRadians(150))*(50 + RADIO))),pincel);

        //Dibuja linea inferior
        canvas.drawLine(0,getHeight()-3,getWidth(),getHeight()-3,pincel);

        //Dibuja los circulos
        pincel.setStrokeWidth(5);
        canvas.drawCircle(getWidth()/2,getHeight(),RADIO-600,pincel);
        canvas.drawCircle(getWidth()/2,getHeight(),RADIO-400,pincel);
        canvas.drawCircle(getWidth()/2,getHeight(),RADIO-200,pincel);
        canvas.drawCircle(getWidth()/2,getHeight(),RADIO,pincel);

        //Dibuja los numeros alrrededor de la circunferencia
        Path trazado = new Path();
        trazado.addCircle(getWidth()/2,getHeight(),70+RADIO, Path.Direction.CW);
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
    }


}
