package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Vector;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {

    private final int gemi_turu_adedi = 4;
    private Bitmap arkaplan, gemi_resmi[], dusman_gemi,
            gemi_ates, dusman_gemi_ates;
    //Geminin koordinatlarının ve hızının bulunduğu değişken
    private int gemix, gemiy, gemi_hedefx, gemi_hedefy, gemi_hizy;
    //Düşmanın koordinatlarının ve hızının bulunduğu değişken
    private int dusman_gemix, dusman_gemiy, dusman_gemi_hedefx,
            dusman_gemi_hedefy, dusman_gemi_hizy;
    //Ateş efektinin x ve y koordinatları
    private int gemi_atesx, gemi_atesy,
            dusman_gemi_atesx, dusman_gemi_atesy;
    //Ateş efektinin hedeflerinin x ve y koordinatları
    private int gemi_ates_hedefx, gemi_ates_hedefy,
            dusman_gemi_ates_hedefx, dusman_gemi_ates_hedefy;
    private int gemi_ates_hizx, gemi_ates_hizy,
            dusman_gemi_ates_hizx, dusman_gemi_ates_hizy;
    //Gemilerin birbirlerinin menziline girip girmediklerini ölçen değişken
    private int tetikleyici, dusman_tetikleyici;

    private int[] baslangicx, baslangicy, sinirx, siniry;

    private int gemi_sec1;

    private boolean tetiklendimi, dusman_tetiklendimi,
            dusman_gemi_yoket, gemi_yoket;

    private Vector<Vector<Integer>> gemiler;


    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        Log.i(TAG, "setup");
        //Oyuncu gemi resmi parametreleri
        gemix = 160;
        gemiy = 180;
        gemi_hedefx = 160;
        gemi_hedefy = 1230;
        gemi_hizy = 10;
        //Düşman gemi resmi parametreleri
        dusman_gemix = 190;
        dusman_gemiy = 1270;
        dusman_gemi_hedefx = 710;
        dusman_gemi_hedefy = 200;
        dusman_gemi_hizy = 10;
        //
        tetikleyici = 0;
        dusman_tetikleyici = 0;

        tetiklendimi = false;
        dusman_tetiklendimi = false;
        dusman_gemi_yoket = false;
        gemi_yoket = false;

        gemi_ates_hedefx = 0;
        gemi_ates_hedefy = 0;
        dusman_gemi_ates_hedefx = 0;
        dusman_gemi_ates_hedefy = 0;

        gemi_ates_hizx = 30;
        gemi_ates_hizy = 15;
        dusman_gemi_ates_hizx = 30;
        dusman_gemi_ates_hizy = 30;

        //Üretileceği gemiyi seçen koordinatlar
        baslangicx = new int[]{70, 325, 580, 835};
        baslangicy = new int[]{1680, 1680, 1680, 1680};
        sinirx = new int[]{300, 555, 810, 1065};
        siniry = new int[]{1900, 1900, 1900, 1900};

        gemi_sec1 = 0;

        arkaplan = Utils.loadImage(root, "map.png");
        gemi_resmi = new Bitmap[gemi_turu_adedi];
        gemi_resmi[0] = Utils.loadImage(root, "ship_1_360_01.png");
        gemi_resmi[1] = Utils.loadImage(root, "ship_7_rot_01.png");
        gemi_resmi[2] = Utils.loadImage(root, "ship_6_ver1_02.png");
        gemi_resmi[3] = Utils.loadImage(root, "ship_3_01.png");
        dusman_gemi = Utils.loadImage(root, "ship_7_rot_01.png");
        gemi_ates = Utils.loadImage(root, "muzzle_top_blue_01.png");
        dusman_gemi_ates = Utils.loadImage(root, "muzzle_top_red_01.png");

        gemiler = new Vector<Vector<Integer>>();
    }

    public void update() {
        Log.i(TAG, "update");

        gemileriIlerlet();
        dusmanGemileriniIlerlet();
        menzilTestleriniYap();
        atesEt();
    }

    public void draw(Canvas canvas) {
        Log.i(TAG, "draw");

        arkaplaniCiz(canvas);
        slotlariCiz(canvas);
        gemileriCiz(canvas);
        dusmanGemileriniCiz(canvas);
        mermileriCiz(canvas);
        dusmanMermileriniCiz(canvas);
    }

    //Arkaplanı çizen method
    private void arkaplaniCiz(Canvas canvas) {
        canvas.drawBitmap(arkaplan, 0, 0, null);
    }

    //Yeni gemi_resmi üreten method
    private void gemiUret(int gemi_turu) {
        //gemi_yoket = false;
        gemix = 160;
        gemiy = 180;
        Vector<Integer> yeni_gemi = new Vector<>();
        yeni_gemi.add(gemix);
        yeni_gemi.add(gemiy);
        yeni_gemi.add(gemi_turu);
        gemiler.add(yeni_gemi);
    }

    //Yeni düşman gemisi üreten method
    private void dusmanGemiUret() {
        //dusman_gemi_yoket = false;
        dusman_gemix = 190;
        dusman_gemiy = 1270;
    }

    //Gemiyi çizen method
    private void gemileriCiz(Canvas canvas) {
        for (int i = 0; i < gemiler.size(); i++) {
            canvas.drawBitmap(
                    gemi_resmi[gemiler.get(i).get(2)],
                    gemiler.get(i).get(0) + (((400 - gemiler.get(i).get(0))
                            - gemi_resmi[gemiler.get(i).get(2)].getWidth()) / 2),
                    gemiler.get(i).get(1) + (((430 - gemiler.get(i).get(1))
                            - gemi_resmi[gemiler.get(i).get(2)].getHeight()) / 2),
                    null);
        }           //Yukarıdaki 400 yazan yer geminin oluştuğu yerin x ekseninin sınırı.
                    //Yukarıdaki 430 yazan yer geminin oluştuğu yerin y ekseninin sınırı.
        if (gemi_yoket == false) {
            canvas.drawBitmap(gemi_resmi[0], gemix, gemiy, null);
        } else {
            dusman_tetiklendimi = false;
        }
        /*if (gemi_sec1 == 1) {
            canvas.drawBitmap(gemi_resmi, gemix, gemiy, null);
        }*/
    }

    private void slotlariCiz(Canvas canvas) {
        for (int i = 0; i < gemi_turu_adedi; i++) {
            canvas.drawBitmap(gemi_resmi[i],
                    baslangicx[i] + (((sinirx[i] - baslangicx[i]) - gemi_resmi[i].getWidth()) / 2),
                    baslangicy[i] + (((siniry[i] - baslangicy[i]) - gemi_resmi[i].getHeight()) / 2),
                    null);
        }
    }

    //Düşman gemisini çizen method
    private void dusmanGemileriniCiz(Canvas canvas) {
        if (dusman_gemi_yoket == false) {
            canvas.drawBitmap(dusman_gemi, dusman_gemix, dusman_gemiy, null);
        } else {
            tetiklendimi = false;
        }
    }

    //Oyuncu gemisinin ateşini çizen method
    private void mermileriCiz(Canvas canvas) {
        if (tetiklendimi && dusman_gemi_yoket == false) {
            canvas.drawBitmap(gemi_ates, gemi_atesx, gemi_atesy, null);
        }
    }

    //Düşman gemisinin ateşini çizen method
    private void dusmanMermileriniCiz(Canvas canvas) {
        if (dusman_tetiklendimi && gemi_yoket == false) {
            canvas.drawBitmap(dusman_gemi_ates, dusman_gemi_atesx, dusman_gemi_atesy, null);
        }
    }

    //Oyuncu gemisinin ilerleyişi
    private void gemileriIlerlet() {
        if ((gemiy < gemi_hedefy) && (!tetiklendimi)) gemiy += gemi_hizy;
    }

    //Düşman gemisinin ilerleyişi
    private void dusmanGemileriniIlerlet() {
        if ((dusman_gemiy > dusman_gemi_hedefy) && (!dusman_tetiklendimi))
            dusman_gemiy -= dusman_gemi_hizy;
    }

    //İki gemi_resmi arasındaki mesafeyi ölçen method
    private void menzilTestleriniYap() {
        tetikleyici = Math.abs(gemiy - dusman_gemiy);
        dusman_tetikleyici = Math.abs(dusman_gemiy - gemiy);

        if (tetikleyici < 400) {
            tetiklendimi = true;
        }

        if (dusman_tetikleyici < 400) {
            dusman_tetiklendimi = true;
        }
    }

    //Ateş efektini üreten method
    private void atesEt() {
        //Ateş etme mekanizmasının başlangıcı
        if (tetiklendimi == false) {
            gemi_atesx = gemix + 35;
            gemi_atesy = gemiy + 54;
        }

        if (tetiklendimi == true) {
            //gemi_atesx = gemi_atesx;
            gemi_atesy = gemi_atesy + gemi_ates_hizy;

            if ((Math.abs(gemi_atesy - dusman_gemiy)) < 40) {
                tetiklendimi = false;
                dusman_gemi_yoket = true;
            }
        }

        if (dusman_tetiklendimi == false) {
            dusman_gemi_atesx = dusman_gemix;
            dusman_gemi_atesy = dusman_gemiy - 100;
        }

        if (dusman_tetiklendimi == true) {
            dusman_gemi_atesy = dusman_gemi_atesy - dusman_gemi_ates_hizy;

            if ((Math.abs(dusman_gemi_atesy - gemiy)) < 40) {
                dusman_tetiklendimi = false;
                gemi_yoket = true;
            }
        }

        gemi_ates_hedefx = dusman_gemix;
        gemi_ates_hedefy = dusman_gemiy;
    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public boolean backPressed() {
        return true;
    }

    public void surfaceChanged(int width, int height) {

    }

    public void surfaceCreated() {

    }

    public void surfaceDestroyed() {

    }

    public void touchDown(int x, int y, int id) {

    }

    public void touchMove(int x, int y, int id) {

    }

    public void touchUp(int x, int y, int id) {
        gemi_sec1 = (x - 70) / 255;
        gemiUret(gemi_sec1);
        //gemi_sec1 = 2;
        //dusmanGemiUret();
    }


    public void pause() {

    }


    public void resume() {

    }


    public void reloadTextures() {

    }


    public void showNotify() {

    }

    public void hideNotify() {

    }

}
