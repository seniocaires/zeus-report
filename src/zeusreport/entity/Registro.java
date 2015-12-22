package zeusreport.entity;

/**
 * Entidade para representar os registros de ponto dos usu�rios do Zeus.
 * @author Senio Caires
 */
public class Registro {

	/**
	 * Data formatada.
	 * @author Senio Caires
	 */
	private String dataFormatada;

	/**
	 * Hor�rio do registro.
	 * @author Senio Caires
	 */
	private String horas;

	/**
	 * Dono do registro de ponto.
	 * @author Senio Caires
	 */
	private UsuarioZeus usuario;

	/**
	 * Retorna a data formatada do registro.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getDataFormatada() {
		return dataFormatada;
	}

	/**
	 * Retorna o hor�rio do registro de ponto.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHoras() {
		return horas;
	}

	/**
	 * Retorna o usu�rio.
	 * @author Senio Caires
	 * @return
	 */
	public final UsuarioZeus getUsuario() {
		return usuario;
	}

	/**
	 * Altera o usu�rio
	 * @author Senio Caires
	 * @param usuarioParametro - Usu�rio
	 */
	public final void setUsuario(final UsuarioZeus usuarioParametro) {
		this.usuario = usuarioParametro;
	}
}
