package consulta.dominio;

public enum EspecialidadeEnum {

    CARDIOLOGIA("Cardiologia"),
    CLINICO_GERAL("Clínico Geral"),
    FISIOTERAPIA("Fisioterapia"),
    ODONTOLOGIA("Odontologia");

    private final String descricao;

    EspecialidadeEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}