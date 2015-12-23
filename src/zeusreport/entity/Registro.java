package zeusreport.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sporeon.baseutil.DataUtil;

/**
 * Entidade para representar os registros de ponto dos usuários do Zeus.
 * @author Senio Caires
 */
public class Registro {

	/**
	 * Dono do registro de ponto.
	 * @author Senio Caires
	 */
	private UsuarioZeus usuario;

	/**
	 * Data do registro.
	 * @author Senio Caires
	 */
	private Date data;

	/**
	 * Horários registrados.
	 * @author Senio Caires
	 */
	private List<String> horarios;

	/**
	 * Retorna o usuário.
	 * @author Senio Caires
	 * @return
	 */
	public final UsuarioZeus getUsuario() {
		return usuario;
	}

	/**
	 * Altera o usuário
	 * @author Senio Caires
	 * @param usuarioParametro - Usuário
	 */
	public final void setUsuario(final UsuarioZeus usuarioParametro) {
		this.usuario = usuarioParametro;
	}

	/**
	 * Retorna a data.
	 * @author Senio Caires
	 * @return {@link Date}
	 */
	public final Date getData() {
		return data;
	}

	/**
	 * Altera a data.
	 * @author Senio Caires
	 * @param dataParametro - Data do registro
	 */
	public final void setData(final Date dataParametro) {
		this.data = dataParametro;
	}

	/**
	 * Retorna a lista de horários.
	 * @author Senio Caires
	 * @return {@link List}<{@link String}>
	 */
	public final List<String> getHorarios() {

		if (horarios == null) {
			horarios = new ArrayList<String>();
		}

		return horarios;
	}

	/**
	 * Altera a lista de horários.
	 * @author Senio Caires
	 * @param horariosParametro - Horários
	 */
	public final void setHorarios(final List<String> horariosParametro) {
		this.horarios = horariosParametro;
	}

	/**
	 * Retorna a lista de horários.
	 * @author Senio Caires
	 * @return {@link List}<{@link Date}>
	 */
	public final List<Date> getHorariosComData() {

		List<Date> resultado = new ArrayList<Date>();

		for (String horario : getHorarios()) {
			resultado.add(DataUtil.stringParaDate(DataUtil.dateParaString(getData()) + " " + horario + ":00", "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm"));
		}

		return resultado;
	}
}
