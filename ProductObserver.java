package com.company.inventory3.Observer;

public interface ProductObserver {
    void onStockChange(int productId, int newQuantity);
}
