package zeusreport.entity;

import java.util.Date;

import com.sporeon.baseutil.DataUtil;

/**
 * Entidade para representar as datas previstas.
 * @author Senio Caires
 */
public class Previsto {

	/**
	 * Data formatada.
	 * @author Senio Caires
	 */
	private String dataFormatada;

	/**
	 * Horas previstas.
	 * @author Senio Caires
	 */
	private String horas;

	/**
	 * Retorna a data.
	 * @author Senio Caires
	 * @return {@link Date}
	 */
	public final Date getData() {
		return DataUtil.stringParaDate(getDataFormatada());
	}

	/**
	 * Retorna a data formatada.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getDataFormatada() {
		return dataFormatada;
	}

	/**
	 * Retorna as horas previstas.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHoras() {
		return horas;
	}
}
