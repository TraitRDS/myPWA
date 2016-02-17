package com.ricky.traitphotos.app;

import com.ricky.traitphotos.dialog.TraitPicasaDialog;

/**
 * Created by yli on 1/28/2016.
 */
public class UIApp {
    public static void main(String[] args) {
        TraitPicasaDialog dialog = new TraitPicasaDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
