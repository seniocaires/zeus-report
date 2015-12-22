package zeusreport.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade para representar os usu�rios do Zeus.
 * @author Senio Caires
 */
public class UsuarioZeus {

	/**
	 * C�digo do usu�rio.
	 * @author Senio Caires
	 */
	private Integer codigo;

	/**
	 * Nome do usu�rio.
	 * @author Senio Caires
	 */
	private String nome;

	/**
	 * Login do usu�rio.
	 * @author Senio Caires
	 */
	private String login;

	/**
	 * Senha do usu�rio.
	 * @author Senio Caires
	 */
	private String senha;

	/**
	 * Lista de registros de ponto do usu�rio.
	 * @author Senio Caires
	 */
	private List<Registro> registros;

	/**
	 * Retorna o c�digo do usu�rio.
	 * @author Senio Caires
	 * @return {@link Integer}
	 */
	public final Integer getCodigo() {
		return codigo;
	}

	/**
	 * Altera o c�digo do usu�rio.
	 * @author Senio Caires
	 * @param codigoParametro - C�digo do usu�rio
	 */
	public final void setCodigo(final Integer codigoParametro) {
		this.codigo = codigoParametro;
	}

	/**
	 * Retorna o nome do usu�rio.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getNome() {
		return nome;
	}

	/**
	 * Altera o nome do usu�rio.
	 * @author Senio Caires
	 * @param nomeParametro - Nome do usu�rio
	 */
	public final void setNome(final String nomeParametro) {
		this.nome = nomeParametro;
	}

	/**
	 * Retorna a lista de registros de ponto.
	 * @author Senio Caires
	 * @return {@link List}<{@link Registro}>
	 */
	public final List<Registro> getRegistros() {

		if (registros == null) {
			registros = new ArrayList<Registro>();
		}

		return registros;
	}

	/**
	 * Altera a lista de registros de ponto.
	 * @author Senio Caires
	 * @param registrosParametro - Registros de ponto
	 */
	public final void setRegistros(final List<Registro> registrosParametro) {
		this.registros = registrosParametro;
	}

	/**
	 * Retorna o login do usu�rio.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getLogin() {
		return login;
	}

	/**
	 * Retorna a senha do usu�rio.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getSenha() {
		return senha;
	}
}
