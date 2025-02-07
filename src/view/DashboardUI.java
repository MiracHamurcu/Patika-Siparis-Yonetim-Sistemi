package view;

import business.BasketController;
import business.CartController;
import business.CustomerController;
import business.ProductController;
import core.Helper;
import core.Item;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane Siparişler;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_custome_name;
    private JComboBox<Customer.TYPE> cmb_customer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JButton btn_customer_new;
    private JLabel lbl_f_customer_name;
    private JLabel lbl_f_customer_type;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_product_name;
    private JTextField fld_f_product_code;
    private JComboBox<Item> cmb_product_stock;
    private JButton btn_product_filter;
    private JButton btn_product_filter_reset;
    private JButton btn_product_new;
    private JLabel lbl_f_product_name;
    private JLabel lbl_f_product_code;
    private JLabel lbl_f_product_stock;
    private JPanel pnl_basket;
    private JPanel pnl_basket_top;
    private JScrollPane scrl_basket;
    private JComboBox<Item> cmb_b_customer;
    private JLabel lbl_b_choose_customer;
    private JButton btn_b_clear;
    private JButton btn_b_new;
    private JLabel lbl_b_price;
    private JLabel lbl_b_count;
    private JTable tbl_basket;
    private JScrollPane scrl_cart;
    private JTable tbl_cart;
    private User user;
    private  CustomerController customerController;
    private ProductController productController;
    private BasketController basketController;
    private CartController cartController;
    private DefaultTableModel tmdl_customer = new DefaultTableModel();
    private DefaultTableModel tmdl_product = new DefaultTableModel();
    private DefaultTableModel tmdl_basket = new DefaultTableModel();
    private DefaultTableModel tmdl_cart = new DefaultTableModel();
    private JPopupMenu popup_customer = new JPopupMenu();
    private JPopupMenu popup_product = new JPopupMenu();
    private JPopupMenu popup_cart = new JPopupMenu();

    public DashboardUI(User user){
    this.user = user;
    this.customerController = new CustomerController();
    this.productController = new ProductController();
    this.basketController = new BasketController();
    this.cartController = new CartController();
    if (user == null){
        Helper.showMsg("error");
        dispose();
    }

    this.add(container);
    this.setTitle("Müşteri yönetim sistemi");
    this.setSize(1000,500);
    int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
    int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;

    this.setLocation(x, y);
    this.setVisible(true);

    this.lbl_welcome.setText("Hoşgeldin: "+ this.user.getName());

    this.btn_logout.addActionListener(e -> {
        dispose();
        LoginUI loginUI = new LoginUI();
    });

    //CUSTOMER TAB
        loadCustomerPopupMenu();
        loadCustomerTable(null);
        loadCustomerButtonEvent();
        this.cmb_customer_type.setModel(new DefaultComboBoxModel<>(Customer.TYPE.values()));
        this.cmb_customer_type.setSelectedItem(null);

    //PRODUCT TAB
        loadProductTable(null);

        loadProductPopupMenu();

        loadProductButtonEvent();

        this.cmb_product_stock.addItem(new Item(1,"Stokta Var"));
        this.cmb_product_stock.addItem(new Item(2,"Stokta Yok"));
        this.cmb_product_stock.setSelectedItem(null);

        // basket tab

        loadBasketTable();
        loadBasketButtonEvent();
        loadBasketCustomerCombo();


        // cart tab
        loadCartTable();
        loadCartPopupMenu();
    }
    private void loadCartPopupMenu(){
        this.tbl_cart.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_cart.rowAtPoint(e.getPoint());
                tbl_cart.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        this.tbl_cart.setComponentPopupMenu(this.popup_cart);

        this.popup_cart.add("Sil").addActionListener(e -> {
            int selectId = Integer.parseInt(tbl_cart.getValueAt(tbl_cart.getSelectedRow(), 0).toString());
            if (Helper.confirm("sure")) {
                if (this.cartController.delete(selectId)) {
                    Helper.showMsg("done");
                    loadCartTable();
                } else {
                    Helper.showMsg("error");
                }
            }
        });


    }
    private void loadCartTable(){
        Object[] columnCart =  {"ID", "Müşteri Adı", "Ürün Adı", "Fiyat", "Sipariş Tarihi", "Sipariş Notu"};
        ArrayList<Cart> carts = this.cartController.findAll();
        // tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) tbl_cart.getModel();
        clearModel.setRowCount(0);
        //
        this.tmdl_cart.setColumnIdentifiers(columnCart);

        int totalPrice = 0;
        for (Cart cart: carts) {
            Object[] rowObject = {
                    cart.getId(),
                    cart.getCustomer().getName(),
                    cart.getProduct().getName(),
                    cart.getPrice(),
                    cart.getDate(),
                    cart.getNote()
            };
            this.tmdl_cart.addRow(rowObject);
        }
        this.tbl_cart.setModel(tmdl_cart);
        this.tbl_cart.getTableHeader().setReorderingAllowed(false);
        this.tbl_cart.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_cart.setEnabled(true);

    }
    private void  loadBasketCustomerCombo(){
        ArrayList<Customer> customers = this.customerController.findAll();
        this.cmb_b_customer.removeAllItems();
        for (Customer customer: customers){
            int comboKey = customer.getId();
            String comboValue = customer.getName();
            this.cmb_b_customer.addItem(new Item(comboKey, comboValue));

        }
        this.cmb_b_customer.setSelectedItem(null);
    };

    private void loadBasketButtonEvent(){
        this.btn_b_clear.addActionListener(e -> {
            if (this.basketController.clear()){
                Helper.showMsg("done");
                loadBasketTable();
                this.cmb_b_customer.setSelectedItem(null);
            }else{
                Helper.showMsg("error");
            }
        });
        btn_b_new.addActionListener(e -> {
            Item SelectedCustomer = (Item) this.cmb_b_customer.getSelectedItem();
            if (SelectedCustomer == null){
                Helper.showMsg("Lütfen müşteri seçiniz.");
            }
            else{
                Customer customer = this.customerController.getById(SelectedCustomer.getKey());
                ArrayList<Basket> baskets = this.basketController.findAll();

                if (customer.getId() == 0){
                    Helper.showMsg("Böyle bir müşteri bulunmamaktadır.");
                }
                else if (baskets.size()== 0) {
                    Helper.showMsg("Ürün ekleyin");
                }else{
                    CartUI cartUI = new CartUI(customer);
                    cartUI.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            loadBasketTable();
                            loadCartTable();
                            loadProductTable(null);
                        }
                    });
                }
            }
        });
    };

    private void loadBasketTable(){
        Object[] columnBasket =  {"ID", "Ürün Adı", "Ürün Kodu", "Fiyat", "Stok"};
        ArrayList<Basket> baskets = this.basketController.findAll();
        // tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_basket.getModel();
        clearModel.setRowCount(0);
        //
        this.tmdl_basket.setColumnIdentifiers(columnBasket);

        int totalPrice = 0;
        for (Basket basket: baskets) {
            Object[] rowObject = {
                    basket.getId(),
                    basket.getProduct().getName(),
                    basket.getProduct().getCode(),
                    basket.getProduct().getPrice(),
                    basket.getProduct().getStock()
            };
            this.tmdl_basket.addRow(rowObject);
            totalPrice += basket.getProduct().getPrice();
        }
        this.lbl_b_price.setText(totalPrice + "TL");
        this.lbl_b_count.setText(baskets.size() + "Adet");


        this.tbl_basket.setModel(tmdl_basket);
        this.tbl_basket.getTableHeader().setReorderingAllowed(false);
        this.tbl_basket.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_basket.setEnabled(true);

    }

    private void loadProductTable(ArrayList<Product> products){
        Object[] columnProduct =  {"ID", "Ürün Adı", "Ürün Kodu", "Fiyat", "Stok"};

        if (products == null){
            products = this.productController.findAll();
        }
        // tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_product.getModel();
        clearModel.setRowCount(0);
        //
        this.tmdl_product.setColumnIdentifiers(columnProduct);
        for (Product product: products) {
            Object[] rowObject = {product.getId(),
                    product.getName(),
                    product.getCode(),
                    product.getPrice(),
                    product.getStock()
             };
            this.tmdl_product.addRow(rowObject);
        }
        this.tbl_product.setModel(tmdl_product);
        this.tbl_product.getTableHeader().setReorderingAllowed(false);
        this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_product.setEnabled(true);
    }

    private void loadProductButtonEvent(){
       this.btn_product_new.addActionListener(e -> {
           ProductUI productUI = new ProductUI(new Product());
           productUI.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosed(WindowEvent e) {
                   loadProductTable(null);
               }
           });
        });
       this.btn_product_filter.addActionListener(e -> {
           ArrayList<Product> filterProducts = this.productController.filter(
                   this.fld_f_custome_name.getText(),
                   this.fld_f_product_code.getText(),
                   (Item) this.cmb_product_stock.getSelectedItem()
           );
           loadProductTable(filterProducts);
        });

        btn_product_filter_reset.addActionListener(e -> {
            loadProductTable(null);
            this.fld_product_name.setText(null);
            this.fld_f_product_code.setText(null);
            this.cmb_product_stock.setSelectedItem(null);
            loadProductTable(null);
        });
    }

    private void loadProductPopupMenu(){

        this.tbl_product.setComponentPopupMenu(this.popup_product);
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        this.popup_product.add("Sepete Ekle").addActionListener(e -> {
            int selectId = Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(), 0).toString());
            Product basketProduct = this.productController.getById(selectId);
            if (basketProduct.getStock() <= 0){
                Helper.showMsg("Bu ürün stokta yoktur!");
            }
            else  {
                Basket basket = new Basket(basketProduct.getId());
                if (this.basketController.save(basket)){
                    Helper.showMsg("done");
                    loadBasketTable();
                }
                else{
                    Helper.showMsg("error");
                }
            }
        });
        this.popup_product.add("Güncelle").addActionListener(e -> {
            int selectId = Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(), 0).toString());
            ProductUI productUI = new ProductUI(this.productController.getById(selectId));
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                    loadBasketTable();
                }
            });
        });
        this.popup_product.add("Sil").addActionListener(e -> {
            int selectId = Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(), 0).toString());
            if (Helper.confirm("sure")) {
                if (this.productController.delete(selectId)) {
                    Helper.showMsg("done");
                    loadProductTable(null);
                    loadBasketTable();
                } else {
                    Helper.showMsg("error");
                }
            }
        });

    }

    private void loadCustomerButtonEvent(){
      this.btn_customer_new.addActionListener(e -> {
            CustomerUI customerUI = new CustomerUI(new Customer());
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();

                }
            });
        });
        this.btn_customer_filter.addActionListener(e -> {
            ArrayList<Customer> filterCustomers = this.customerController.filter(
                    this.fld_f_custome_name.getText(),
                    (Customer.TYPE) this.cmb_customer_type.getSelectedItem()
            );
            loadCustomerTable(filterCustomers);
        });
        btn_customer_filter_reset.addActionListener(e -> {
            loadCustomerTable(null);
            this.fld_f_custome_name.setText(null);
            this.cmb_customer_type.setSelectedItem(null);
        });
    }

    private void loadCustomerPopupMenu(){
        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_customer.rowAtPoint(e.getPoint());
                tbl_customer.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        this.tbl_customer.setComponentPopupMenu(this.popup_customer);
        this.popup_customer.add("Güncelle").addActionListener(e -> {
         int selectId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
         CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectId));
         customerUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCustomerTable(null);
                loadBasketCustomerCombo();
            }
        });
     });
     this.popup_customer.add("Sil").addActionListener(e -> {
         int selectId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
         if (Helper.confirm("sure")) {
             if (this.customerController.delete(selectId)) {
                 Helper.showMsg("done");
                 loadCustomerTable(null);
                 loadBasketCustomerCombo();

             } else {
                 Helper.showMsg("error");
             }
         }
     });

    }

    private void loadCustomerTable(ArrayList<Customer> customers){
        Object[] columnCustomer =  {"ID", "Müşteri Adı", "Müşteri Tipi", "Telefon", "E-posta", "Adres"};

        if (customers == null){
            customers = this.customerController.findAll();
        }
        // tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel();
        clearModel.setRowCount(0);
        //
        this.tmdl_customer.setColumnIdentifiers(columnCustomer);
        for (Customer customer : customers) {
            Object[] rowObject = {
                    customer.getId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAddress()
            };
            this.tmdl_customer.addRow(rowObject);
        }
        this.tbl_customer.setModel(tmdl_customer);
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_customer.setEnabled(true);
    }
}
