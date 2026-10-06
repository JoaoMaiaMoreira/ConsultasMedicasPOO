package consulta.dominio;

import consulta.excecao.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    @DisplayName("Deve criar um paciente válido com dados limpos (trimmed)")
    void deveCriarPacienteValido() {
        UUID id = UUID.randomUUID();
        Paciente paciente = new Paciente(id, "  Maria Silva  ", "  123.456.789-00  ", "  (35) 99999-0000  ");

        assertEquals(id, paciente.getId());
        assertEquals("Maria Silva", paciente.getNome());
        assertEquals("123.456.789-00", paciente.getCpf());
        assertEquals("(35) 99999-0000", paciente.getTelefone());
    }

    @Test
    @DisplayName("Deve redefinir telefone nulo para string vazia")
    void deveTratarTelefoneNulo() {
        Paciente paciente = new Paciente(UUID.randomUUID(), "João", "111.222.333-44", null);
        assertEquals("", paciente.getTelefone());
    }

    @Test
    @DisplayName("Deve lançar exceção para id, nome ou CPF nulos/vazios")
    void deveLancarExcecaoParaDadosInvalidos() {
        UUID id = UUID.randomUUID();

        assertThrows(DominioException.class, () -> new Paciente(null, "Maria", "123.456.789-00", ""));
        assertThrows(DominioException.class, () -> new Paciente(id, null, "123.456.789-00", ""));
        assertThrows(DominioException.class, () -> new Paciente(id, "   ", "123.456.789-00", ""));
        assertThrows(DominioException.class, () -> new Paciente(id, "Maria", null, ""));
        assertThrows(DominioException.class, () -> new Paciente(id, "Maria", "   ", ""));
    }

    @Test
    @DisplayName("Deve comparar dois pacientes pelo ID")
    void deveCompararPorId() {
        UUID id = UUID.randomUUID();
        Paciente p1 = new Paciente(id, "Maria", "111.111.111-11", "");
        Paciente p2 = new Paciente(id, "Maria Alterada", "222.222.222-22", "");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}