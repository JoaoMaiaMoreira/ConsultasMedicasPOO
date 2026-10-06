package consulta.excecao;

public class DominioException extends RuntimeException {

    public DominioException(String mensagem) {
        super(mensagem);
    }

    public DominioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}