package consulta.dominio;

import consulta.excecao.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfissionalTest {

    @Test
    @DisplayName("Deve criar um profissional ativo por padrão com especialidade correta")
    void deveCriarProfissionalAtivoPorPadrao() {
        UUID id = UUID.randomUUID();
        Profissional profissional = new Profissional(id, "  Dr. House  ", EspecialidadeEnum.CLINICO_GERAL);

        assertEquals(id, profissional.getId());
        assertEquals("Dr. House", profissional.getNome());
        assertEquals(EspecialidadeEnum.CLINICO_GERAL, profissional.getEspecialidade());
        assertTrue(profissional.isAtivo());
    }

    @Test
    @DisplayName("Deve permitir alternar o status do profissional entre desativado e ativo")
    void deveAlterarStatusAtivo() {
        Profissional profissional = new Profissional(UUID.randomUUID(), "Dr. Strange", EspecialidadeEnum.CARDIOLOGIA);

        profissional.desativar();
        assertFalse(profissional.isAtivo());

        profissional.ativar();
        assertTrue(profissional.isAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção para id, nome ou especialidade nulos/vazios")
    void deveLancarExcecaoParaDadosInvalidos() {
        UUID id = UUID.randomUUID();

        assertThrows(DominioException.class, () -> new Profissional(null, "Dr. Nome", EspecialidadeEnum.ODONTOLOGIA));
        assertThrows(DominioException.class, () -> new Profissional(id, null, EspecialidadeEnum.ODONTOLOGIA));
        assertThrows(DominioException.class, () -> new Profissional(id, "   ", EspecialidadeEnum.ODONTOLOGIA));
        assertThrows(DominioException.class, () -> new Profissional(id, "Dr. Nome", null));
    }
}