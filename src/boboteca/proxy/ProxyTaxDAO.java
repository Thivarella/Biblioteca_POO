package boboteca.proxy;

import boboteca.dao.TaxDAO;
import boboteca.model.Tax;

import java.util.List;

public class ProxyTaxDAO extends TaxDAO {

    private static ProxyTaxDAO instance;
    private List<Tax> cache;
    private List<Tax> cacheByUser;

    @Override
    public void insertTax(Tax tax) {
        super.insertTax(tax);
        cache = null;
    }

    @Override
    public List<Tax> findAllTax() {
        if (cache == null)
            cache = super.findAllTax();
        return cache;
    }

    public void setCache(List<Tax> cache) {
        this.cache = cache;
    }

    @Override
    public List<Tax> findAllTaxByUserId(Integer userId, Boolean isPaid) {
        if (cacheByUser == null)
            cacheByUser = super.findAllTaxByUserId(userId,isPaid);
        return cacheByUser;
    }

    public static synchronized ProxyTaxDAO getInstance() {
        if(instance == null)
            instance = new ProxyTaxDAO();
        return instance;
    }
}
