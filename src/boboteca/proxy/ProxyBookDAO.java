package boboteca.proxy;

import boboteca.dao.BookDAO;
import boboteca.model.Book;

import java.util.List;

public class ProxyBookDAO extends BookDAO {

    private static ProxyBookDAO instance;
    private List<Book> cache;

    @Override
    public void insertBook(Book book) {
        super.insertBook(book);
        cache = null;
    }

    @Override
    public void updateBook(Book book) {
        super.updateBook(book);
        cache = null;
    }

    @Override
    public List<Book> findAllBooks(String search, String filter) {
        if (cache == null)
            cache = super.findAllBooks(search,filter);
        return cache;
    }

    public void setCache(List<Book> cache) {
        this.cache = cache;
    }

    public static synchronized ProxyBookDAO getInstance() {
        if(instance == null)
            instance = new ProxyBookDAO();
        return instance;
    }
}
