package com.akrivonos.app_standart_java.models;

public class SettingsLoadPage {

    private int currentPage;
    private int pagesAmount;
    private int typeLoadPage;

    public SettingsLoadPage(int currentPage, int pagesAmount, int typeLoadPage) {
        this.currentPage = currentPage;
        this.pagesAmount = pagesAmount;
        this.typeLoadPage = typeLoadPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPagesAmount() {
        return pagesAmount;
    }

    public int getTypeLoadPage() {
        return typeLoadPage;
    }

}
