package view;

import javax.swing.*;

import business.ProductController;
import core.Helper;
import entity.Customer;
import entity.Product;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProductUI extends JFrame {
    private JPanel container;
    private JLabel lbl_title;
    private JTextField fld_p_name;
    private JTextField fld_p_code;
    private JTextField fld_p_price;
    private JTextField fld_p_stock;
    private JButton btn_p_save;
    private JLabel lbl_p_name;
    private JLabel lbl_p_code;
    private JLabel lbl_p_price;
    private JLabel lbl_p_stock;
    private Product product;
    private ProductController productController;

    public ProductUI(Product product) {
        this.product = product;
        this.productController = new ProductController();

        this.add(container);
        this.setTitle("Ürün Ekle/Düzenle");
        this.setSize(300, 350);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;
        this.setLocation(x, y);
        this.setVisible(true);

        if (this.product.getId() ==0){
            this.lbl_title.setText("Ürün Ekle");
        } else {
            this.lbl_title.setText("Ürün  Düzenle");
            this.fld_p_name.setText(this.product.getName());
            this.fld_p_code.setText(this.product.getCode());
            this.fld_p_price.setText(String.valueOf(this.product.getPrice()));
            this.fld_p_stock.setText(String.valueOf(this.product.getStock()));
        }
        btn_p_save.addActionListener(e -> {
            JTextField[] checkList = {this.fld_p_name, this.fld_p_code, this.fld_p_price, this.fld_p_stock};
            if (Helper.isFieldListEmpty(checkList)){
                Helper.showMsg("fill");
            }else{


                boolean result = false;
                this.product.setName(this.fld_p_name.getText());
                this.product.setCode(this.fld_p_code.getText());
                this.product.setPrice(Integer.parseInt(this.fld_p_price.getText()));
                this.product.setStock(Integer.parseInt(this.fld_p_stock.getText()));

            if (this.product.getId() == 0){
                result = this.productController.save(this.product);
            }else {
                result = this.productController.update(this.product);
            }
            if (result){
                Helper.showMsg("done");
                dispose();
            }
            else {
                Helper.showMsg("error");
            }
            }
        });
    }
}
