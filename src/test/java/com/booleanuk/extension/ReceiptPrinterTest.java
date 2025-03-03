package com.booleanuk.extension;

import com.booleanuk.core.Basket;
import com.booleanuk.core.Item;
import com.booleanuk.core.Store;
import com.booleanuk.core.items.Bagel;
import com.booleanuk.core.items.Filling;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Locale;

public class ReceiptPrinterTest {

    @BeforeAll
    public static void setUp() {
        // Set locale to UK so there is a dot and not a comma in the prices
        Locale.setDefault(Locale.UK);
    }

    @Test
    public void testCorrectPrintOutput() throws FileNotFoundException, URISyntaxException {
        Store store = new Store("Bob's Bagels");
        Basket basket = new Basket();

        basket.addItem(store.getItemBySKU("BGLP"));
        Bagel bagelPlain1 = (Bagel) basket.addItem(store.getItemBySKU("BGLP"));
        basket.addItem(store.getItemBySKU("BGLP"));
        Bagel bagelSesame = (Bagel) basket.addItem(store.getItemBySKU("BGLS"));
        basket.addFillingToBagel(bagelPlain1, (Filling) store.getItemBySKU("FILE"));
        basket.addFillingToBagel(bagelSesame, (Filling) store.getItemBySKU("FILB"));
        basket.addItem(store.getItemBySKU("COFL"));

        ReceiptPrinter receiptPrinter = new ReceiptPrinter(store, basket);
        String printedReceipt = receiptPrinter.print();
        // System.out.println(printedReceipt);

        // Check for key elements, specific items, and correct cost in the receipt
        Assertions.assertTrue(printedReceipt.contains("Bob's Bagels"));
        Assertions.assertTrue(printedReceipt.contains("Total"));
        Assertions.assertTrue(printedReceipt.contains("Thank you"));

        Assertions.assertTrue(printedReceipt.contains("Latte"));
        Assertions.assertTrue(printedReceipt.contains("⋅ Egg"));
        Assertions.assertTrue(printedReceipt.contains("Sesame"));

        Assertions.assertTrue(printedReceipt.contains("£3.19"));
    }

    @Test
    public void doesPrintReceiptWithDiscounts() throws FileNotFoundException, URISyntaxException {
        Store store = new Store("Bob's Bagels");
        Basket basket = new Basket();

        Item bagelEverything = store.getItemBySKU("BGLP");
        for (int i = 0; i < 16; i++) {
            basket.addItem(bagelEverything);
        }

        ReceiptPrinter receiptPrinter = new ReceiptPrinter(store, basket);
        String printedReceipt = receiptPrinter.print();
        // System.out.println(printedReceipt);

        Assertions.assertTrue(printedReceipt.contains("Bob's Bagels"));
        Assertions.assertTrue(printedReceipt.contains("Total"));
        Assertions.assertTrue(printedReceipt.contains("Thank you"));

        Assertions.assertTrue(printedReceipt.contains("Plain\t\t\t12"));
        Assertions.assertTrue(printedReceipt.contains("Discount\t\t"));
        Assertions.assertTrue(printedReceipt.contains("You saved £"));
    }
}
