import com.example.salem2025.repository.UserAccountRepository;
import com.example.salem2025.repository.entity.UserAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    public UserAccountEntity getByUsername(String username) {
        return userAccountRepository.findByUsername(username).orElse(null);
    }
}
