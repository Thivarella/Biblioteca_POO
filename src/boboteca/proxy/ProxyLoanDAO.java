package boboteca.proxy;

import boboteca.dao.LoanDAO;
import boboteca.model.Loan;

import java.util.List;

public class ProxyLoanDAO extends LoanDAO {

    private static ProxyLoanDAO instance;
    private List<Loan> cache;
    private List<Loan> cacheByUser;
    private Integer id;

    @Override
    public void insertLoan(Loan loan) {
        super.insertLoan(loan);
        cache = null;
    }

    @Override
    public List<Loan> findAllLoan() {
        if (cache == null)
            cache = super.findAllLoan();
        return cache;
    }

    public void setCache(List<Loan> cache) {
        this.cache = cache;
    }

    @Override
    public List<Loan> findAllLoanByUserId(Integer userId) {
        if (cacheByUser == null) {
            cacheByUser = super.findAllLoanByUserId(userId);
        }else if(this.id != userId) {
            this.id = userId;
            cacheByUser = super.findAllLoanByUserId(userId);
        }
        return cacheByUser;
    }

    public static synchronized ProxyLoanDAO getInstance() {
        if(instance == null)
            instance = new ProxyLoanDAO();
        return instance;
    }
}
