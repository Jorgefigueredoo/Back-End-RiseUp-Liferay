import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.eventos.eventos.repository.ContatoRepository; 
import com.eventos.eventos.model.Contato;

@Service
public class ContatoService {
    
    @Autowired
    private ContatoRepository repository;

    public void salvarContato(Contato contato) {
        // Aqui você poderia adicionar lógica extra, como enviar um e-mail para o admin
        repository.save(contato);
    }
}