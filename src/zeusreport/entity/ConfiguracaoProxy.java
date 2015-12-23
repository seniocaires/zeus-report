package zeusreport.entity;

/**
 * Entidade para representar as configurações de proxy.
 * @author Senio Caires
 */
public class ConfiguracaoProxy {

	/**
	 * Define se utiliza proxy autenticado.
	 * @author Senio Caires
	 */
	private Boolean ativa;

	/**
	 * Host do proxy.
	 * @author Senio Caires
	 */
	private String host;

	/**
	 * Porta do proxy.
	 * @author Senio Caires
	 */
	private String porta;

	/**
	 * Login do usuário do proxy.
	 * @author Senio Caires
	 */
	private String login;

	/**
	 * Senha do usuário do proxy.
	 * @author Senio Caires
	 */
	private String senha;

	/**
	 * Retorna a definição se utilizará uma conexão com proxy.
	 * @author Senio Caires
	 * @return {@link Boolean}
	 */
	public Boolean getAtiva() {
		return ativa;
	}

	/**
	 * Retorna o host.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Retorna a porta.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public String getPorta() {
		return porta;
	}

	/**
	 * Retorna o login.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Retorna a senha.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getSenha() {
		return senha;
	}
}
