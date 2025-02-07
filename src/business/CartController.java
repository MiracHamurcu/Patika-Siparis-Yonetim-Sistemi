package business;

import core.Helper;
import dao.CartDao;
import entity.Cart;
import entity.Product;

import java.util.ArrayList;

public class CartController {
    private final CartDao cartDao = new CartDao();
    public Cart getById(int id){
        return this.cartDao.getById(id);
    }

    public boolean save(Cart cart){
        return this.cartDao.save(cart);
    }
    public ArrayList<Cart> findAll(){
        return this.cartDao.findAll();
    }
    public boolean delete(int id){
        if (this.getById(id) == null){
            Helper.showMsg(id+ "ID kayıtlı müşteri bulunamadı");
            return false;
        }
        return this.cartDao.delete(id);
    }
}
