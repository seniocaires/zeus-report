package zeusreport.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import zeusreport.entity.ConfiguracaoProxy;
import zeusreport.entity.Previsto;
import zeusreport.entity.UsuarioZeus;

/**
 * Classe utilitária com as configurações.
 * @author Senio Caires
 */
public class ConfiguracaoUtil {

	/**
	 * Logger.
	 * @author Senio Caires
	 */
	private static final Logger logger = Logger.getLogger(ConfiguracaoUtil.class);

	/**
	 * Type para lista de previstos.
	 * @author Senio Caires
	 */
	private static final Type PREVISTO_TYPE = new TypeToken<List<Previsto>>() { }.getType();

	/**
	 * Type para usuário do Zeus.
	 * @author Senio Caires
	 */
	private static final Type USUARIO_ZEUS_TYPE = new TypeToken<UsuarioZeus>() { }.getType();

	/**
	 * Type para configuração do proxy.
	 * @author Senio Caires
	 */
	private static final Type CONFIGURACAO_PROXY_TYPE = new TypeToken<ConfiguracaoProxy>() { }.getType();

	/**
	 * Lista com as datas e horas previstas.
	 * @author Senio Caires
	 */
	private static List<Previsto> previstos;

	/**
	 * Usuário do Zeus.
	 * @author Senio Caires
	 */
	private static UsuarioZeus usuarioZeus;

	/**
	 * Configuração do proxy.
	 * @author Senio Caires
	 */
	private static ConfiguracaoProxy configuracaoProxy;

	/**
	 * Retorna a lista com as datas e horas previstas.
	 * @author Senio Caires
	 * @return {@link List}<{@link Previsto}>
	 */
	public static final List<Previsto> getPrevistos() {

		if (previstos == null) {

			Gson gson = new Gson();
			JsonReader jsonReader;
			try {
				jsonReader = new JsonReader(new FileReader("configuracao-datas-previstas.json"));
				previstos = gson.fromJson(jsonReader, PREVISTO_TYPE);
			} catch (FileNotFoundException e) {
				logger.error("Erro ao carregar lista de datas previstas do aquivo configuracao-datas-previstas.json");
			}
		}

		return previstos;
	}

	/**
	 * Retorna o usuário do Zeus.
	 * @author Senio Caires
	 * @return {@link UsuarioZeus}
	 */
	public static final UsuarioZeus getUsuarioZeus() {

		if (usuarioZeus == null) {

			Gson gson = new Gson();
			JsonReader jsonReader;
			try {
				jsonReader = new JsonReader(new FileReader("configuracao-usuario-zeus.json"));
				usuarioZeus = gson.fromJson(jsonReader, USUARIO_ZEUS_TYPE);
			} catch (FileNotFoundException e) {
				logger.error("Erro ao carregar o usuário do Zeus do aquivo configuracao-usuario-zeus.json");
			}
		}

		return usuarioZeus;
	}

	/**
	 * Retorna a configuração do proxy.
	 * @author Senio Caires
	 * @return {@link ConfiguracaoProxy}
	 */
	public static ConfiguracaoProxy getConfiguracaoProxy() {

		if (configuracaoProxy == null) {

			Gson gson = new Gson();
			JsonReader jsonReader;
			try {
				jsonReader = new JsonReader(new FileReader("configuracao-proxy.json"));
				configuracaoProxy = gson.fromJson(jsonReader, CONFIGURACAO_PROXY_TYPE);
			} catch (FileNotFoundException e) {
				logger.error("Erro ao carregar o usuário do Zeus do aquivo configuracao-proxy.json");
			}
		}

		return configuracaoProxy;
	}
}
