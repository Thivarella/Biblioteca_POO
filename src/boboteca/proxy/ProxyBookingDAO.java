package boboteca.proxy;

import boboteca.dao.BookingDAO;
import boboteca.model.Booking;

import java.util.List;

public class ProxyBookingDAO extends BookingDAO {

    private static ProxyBookingDAO instance;
    private List<Booking> cache;
    private List<Booking> cacheUserId;

    @Override
    public void insertBooking(Booking booking) {
        super.insertBooking(booking);
        cache = null;
    }

    @Override
    public List<Booking> findAllBooking() {
        if (cache == null)
            cache = super.findAllBooking();
        return cache;
    }

    @Override
    public List<Booking> findAllBookingByUserId(Integer userId) {
        if (cacheUserId == null)
            cacheUserId = super.findAllBookingByUserId(userId);
        return cacheUserId;
    }

    public void setCache(List<Booking> cache) {
        this.cache = cache;
        this.cacheUserId = cache;
    }

    public static synchronized ProxyBookingDAO getInstance() {
        if(instance == null)
            instance = new ProxyBookingDAO();
        return instance;
    }
}
