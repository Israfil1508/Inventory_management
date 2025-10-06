package com.company.inventory3.Observer;

import java.util.ArrayList;
import java.util.List;

public class ProductSubject {
    private static final List<ProductObserver> observers = new ArrayList<>();

    public static void addObserver(ProductObserver observer) {
        observers.add(observer);
    }

    public static void removeObserver(ProductObserver observer) {
        observers.remove(observer);
    }

    public static void notifyObservers(int productId, int newQuantity) {
        for (ProductObserver observer : observers) {
            observer.onStockChange(productId, newQuantity);
        }
    }
}
