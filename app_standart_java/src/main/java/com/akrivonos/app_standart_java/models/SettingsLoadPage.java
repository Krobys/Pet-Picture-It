package com.akrivonos.app_standart_java.models;

public class SettingsLoadPage {

    private final int currentPage;
    private final int pagesAmount;
    private final int typeLoadPage;

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
