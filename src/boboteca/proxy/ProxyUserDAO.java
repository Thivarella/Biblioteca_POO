package boboteca.proxy;

import boboteca.dao.UserDAO;
import boboteca.model.User;

import java.util.List;

public class ProxyUserDAO extends UserDAO {

    private static ProxyUserDAO instance;
    private List<User> cache;

    @Override
    public boolean insertUser(User user) {
        cache = null;
        return super.insertUser(user);
    }

    @Override
    public Boolean updateUser(User user) {
        cache = null;
        return super.updateUser(user);
    }

    @Override
    public List<User> findAllUsers() {
        if (cache == null)
            cache = super.findAllUsers();
        return cache;
    }

    public void setCache(List<User> cache) {
        this.cache = cache;
    }

    public static synchronized ProxyUserDAO getInstance() {
        if(instance == null)
            instance = new ProxyUserDAO();
        return instance;
    }

}
