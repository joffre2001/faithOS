package com.obysoft.faithOS.security;
import java.time.*;import java.util.concurrent.*;import org.springframework.stereotype.Service;

@Service
public class AbuseProtectionService {
    private record Counter(int attempts,Instant windowStart,Instant blockedUntil){}
    private final ConcurrentMap<String,Counter> counters=new ConcurrentHashMap<>();
    public void requireLoginAllowed(String key){Counter c=counters.get("login:"+key);if(c!=null&&c.blockedUntil()!=null&&c.blockedUntil().isAfter(Instant.now()))throw new IllegalArgumentException("Too many login attempts. Try again later.");}
    public void loginFailed(String key){String k="login:"+key;Instant now=Instant.now();counters.compute(k,(ignored,c)->{if(c==null||c.windowStart().plus(Duration.ofMinutes(15)).isBefore(now))return new Counter(1,now,null);int next=c.attempts()+1;return new Counter(next,c.windowStart(),next>=5?now.plus(Duration.ofMinutes(15)):null);});}
    public void loginSucceeded(String key){counters.remove("login:"+key);}
    public void requireRegistrationAllowed(String ip){String k="register:"+ip;Instant now=Instant.now();Counter c=counters.compute(k,(ignored,old)->old==null||old.windowStart().plus(Duration.ofHours(1)).isBefore(now)?new Counter(1,now,null):new Counter(old.attempts()+1,old.windowStart(),null));if(c.attempts()>5)throw new IllegalArgumentException("Too many registration attempts. Try again later.");}
}
