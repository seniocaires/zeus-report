package zeusreport.run;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.sporeon.baseutil.DataUtil;
import com.sporeon.baseutil.ManipulacaoUtil;

import zeusreport.entity.Previsto;
import zeusreport.entity.Registro;
import zeusreport.util.ConfiguracaoUtil;
import zeusreport.util.DownloadUtil;

/**
 * Classe para executar o projeto.
 * @author Senio Caires
 */
public class Run {

	/**
	 * Logger.
	 * @author Senio Caires
	 */
	private static final Logger logger = Logger.getLogger(Run.class);

	/**
	 * Executa o projeto.
	 * @author Senio Caires
	 * @param args
	 */
	public static void main(String[] args) {

		if (ConfiguracaoUtil.getConfiguracaoProxy().getAtiva()) {
			ativarConexaoProxy();
		}

		if (acessarZeus()) {

			try {

				DownloadUtil.downloadArquivo(ConfiguracaoUtil.getUrlRelatorio(), getPath(), "report.pdf");

				PdfReader reader = new PdfReader(getPath() + "/report.pdf");
				String conteudoRelatorioZeus = PdfTextExtractor.getTextFromPage(reader, 1);

				try {

					String nome = (conteudoRelatorioZeus.split("Período: " + ConfiguracaoUtil.getDataInicialFormatada() + " " + ConfiguracaoUtil.getDataFinalFormatada())[0]).split("Relatório de horário")[1].trim();

					if (nome != null && !nome.trim().equals("")) {

						preencherDadosUsuarioZeus(conteudoRelatorioZeus, nome);

						imprimirCabecalho();

						List<String> totaisDias = new ArrayList<String>();
						List<String> saldosDias = new ArrayList<String>();
						String saldoAcumulado = "000:00";

						for (Registro registroIndice : ConfiguracaoUtil.getUsuarioZeus().getRegistros()) {

							System.out.print(DataUtil.dateParaString(registroIndice.getData()) + "\t");

							boolean modulo = false;
							Date horarioAnteriorComData = new Date();
							String totalDia = "";
							String diferencaHorarios = "";
							for (Date horarioComDataIndice : registroIndice.getHorariosComData()) {
								System.out.print(DataUtil.dateParaString(horarioComDataIndice, "HH:mm") + "\t\t");

								if (modulo) {

									List<String> horariosComDataParaSoma = new ArrayList<String>();
									horariosComDataParaSoma.add(DataUtil.dateParaString(horarioAnteriorComData, "HH:mm:ss"));
									horariosComDataParaSoma.add(DataUtil.dateParaString(horarioComDataIndice, "HH:mm:ss"));
									diferencaHorarios = diferencaHorarios(DataUtil.dateParaString(horarioAnteriorComData, "MM/dd/yyyy HH:mm:ss"), DataUtil.dateParaString(horarioComDataIndice, "MM/dd/yyyy HH:mm:ss"));

									if (!totalDia.equals("")) {
										totalDia = somaHorarios(totalDia, diferencaHorarios, 2);
									} else {
										totalDia = diferencaHorarios;
									}
								}

								modulo = !modulo;
								horarioAnteriorComData = horarioComDataIndice;
							}

							int quantidadeTabulacao = 0;

							if (registroIndice.getHorarios().size() > 0) {
								quantidadeTabulacao = 6 - registroIndice.getHorarios().size() - (registroIndice.getHorarios().size() % 2);
							} else {
								quantidadeTabulacao = 6;
							}

							for (int indiceTabulacao = 0 ; indiceTabulacao < quantidadeTabulacao ; indiceTabulacao++) {
								System.out.print("\t\t");
							}

							for (int indiceTabulacao = 0 ; indiceTabulacao < (registroIndice.getHorarios().size() % 2) ; indiceTabulacao++) {
								System.out.print("\t\t");
							}

							if (!totalDia.equals("")) {
								totaisDias.add(totalDia);
								System.out.print(totalDia + "\t");
							} else {
								System.out.print("\t");
							}

							System.out.print("\t");
							String horasDataPrevista = getHorasDataPrevista(DataUtil.dateParaString(registroIndice.getData()), 2);
							System.out.print(horasDataPrevista);

							System.out.print("\t\t");
							String saldoDia = saldoDia((totalDia.equals("") ? "00:00" : totalDia), (horasDataPrevista.equals("") ? "00:00" : horasDataPrevista), 2);
							System.out.print(saldoDia);

							if (!saldoDia.equals("")) {
								saldosDias.add(saldoDia);
							}

							System.out.print("\t\t");
							saldoAcumulado = acumulaSaldoDia(saldoAcumulado, saldoDia);
							System.out.print(saldoAcumulado);

							System.out.println("");
						}

						String somaTotalDia = "00:00";

						for(String totalDiaIndice : totaisDias) {
							somaTotalDia = somaHorarios(ManipulacaoUtil.adicionarChar('0', 6, somaTotalDia, true), ManipulacaoUtil.adicionarChar('0', 6, totalDiaIndice, true), 3);
						}

						System.out.println("\nTOTAL PREVISTO\t" + getTotalHorasPrevistas());
						System.out.println("TOTAL REALIZADO\t" + somaTotalDia);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				logger.error("Erro ao acessar relatório.");
			}

		} else {
			logger.error("Erro ao acessar Zeus.");
			System.exit(0);
		}
	}

	/**
	 * @author Senio Caires
	 * @param conteudoRelatorioZeus
	 * @param nomeUsuarioZeus
	 */
	private static final void preencherDadosUsuarioZeus(String conteudoRelatorioZeus, String nomeUsuarioZeus) {

		ConfiguracaoUtil.getUsuarioZeus().setNome(nomeUsuarioZeus);

		String[] registrosNaoTratados = conteudoRelatorioZeus.split("Entrada Saída Entrada Saída Entrada Saída")[1].split("TOTAIS")[0].split("\n");
		Date dataIndice = ConfiguracaoUtil.getDataInicial();

		for (String registroNaoTratado : registrosNaoTratados) {

			if (!registroNaoTratado.contains(DataUtil.dateParaString(dataIndice))) {
				continue;
			} else {

				Registro registro = new Registro(ConfiguracaoUtil.getUsuarioZeus(), dataIndice);

				String[] horariosNaoTratados = registroNaoTratado.split(DataUtil.dateParaString(dataIndice))[1].trim().split("   ")[0].trim().split(" ");

				for (String horario : horariosNaoTratados) {
					if (horario != null && !horario.trim().equals("")) {
						registro.getHorarios().add(horario.trim());
					}
				}

				ConfiguracaoUtil.getUsuarioZeus().getRegistros().add(registro);
				dataIndice = DataUtil.adicionarDias(dataIndice, 1);
			}
		}
	}

	/**
	 * @author Senio Caires
	 */
	private static final void imprimirCabecalho() {

		imprimirSeparador();

		System.out.println("");

		System.out.println(ConfiguracaoUtil.getUsuarioZeus().getNome());

		imprimirSeparador();

		System.out.println("");
		System.out.println("Data\t\tHorario1\tHorario2\tHorario3\tHorario4\tHorario5\tHorario6\tTotal.Dia\tPrevisto\tSaldo.Dia\tSaldo.Acumulado");

		imprimirSeparador();

		System.out.println("");
	}

	/**
	 * @author Senio Caires
	 */
	private static final void imprimirSeparador() {

		for (int indiceTraco = 1; indiceTraco <= 200 ; indiceTraco++) {
			System.out.print("-");
		}
	}

	/**
	 * @author Senio Caires
	 * @param dataInicio
	 * @param dataTermino
	 * @return {@link String}
	 */
	@SuppressWarnings("unused")
	public static String diferencaHorarios(String dataInicio, String dataTermino) {

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		Date primeiraData = null;
		Date segundaData = null;
		long diferencaSegundos = 0;
		long diferencaoMinutos = 0;
		long diferencaHoras = 0;
		long diferencaDias = 0;

		try {

			primeiraData = format.parse(dataInicio);
			segundaData = format.parse(dataTermino);

			long diferenca = segundaData.getTime() - primeiraData.getTime();

			diferencaSegundos = diferenca / 1000 % 60;
			diferencaoMinutos = diferenca / (60 * 1000) % 60;
			diferencaHoras = diferenca / (60 * 60 * 1000) % 24;
			diferencaDias = diferenca / (24 * 60 * 60 * 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaoMinutos), true);
	}

	/**
	 * @author Senio Caires
	 * @param data
	 * @param digitosHora
	 * @return {@link String}
	 */
	public static String getHorasDataPrevista(String data, int digitosHora) {

		String resultado = "00:00";

		for (Previsto previsto : ConfiguracaoUtil.getPrevistos()) {

			if (DataUtil.dateParaString(previsto.getData()).equals(data)) {
				resultado = previsto.getHoras();
				break;
			}
		}

		return ManipulacaoUtil.adicionarChar('0', digitosHora+3, String.valueOf(resultado), true);
	}

	/**
	 * @author Senio Caires
	 * @param totalDia
	 * @param previsto
	 * @param digitosHora
	 * @return {@link String}
	 */
	public static String saldoDia(String totalDia, String previsto, int digitosHora) {

		String resultado = "00:00";

		int horasTotalDia = Integer.valueOf(totalDia.split(":")[0]);
		int minutosTotalDia = Integer.valueOf(totalDia.split(":")[1]);
		int horasPrevisto = Integer.valueOf(previsto.split(":")[0]);
		int minutosPrevisto = Integer.valueOf(previsto.split(":")[1]);
		int diferencaHoras = 0;
		int diferencaMinutos = 0;
		double totalDiaDouble = Double.valueOf(totalDia.replace(":", "."));
		double totalPrevistoDouble = Double.valueOf(previsto.replace(":", "."));
		String sinal = "";

		if (totalDiaDouble > totalPrevistoDouble) {
			sinal = "+";
		} else if (totalDiaDouble < totalPrevistoDouble) {
			sinal = "-";
		} else {
			sinal = " ";
		}

		if (horasTotalDia > horasPrevisto) {

			diferencaHoras = horasTotalDia - horasPrevisto;

			if (minutosTotalDia > minutosPrevisto) {
				diferencaMinutos = minutosTotalDia - minutosPrevisto;
			} else if (minutosTotalDia < minutosPrevisto) {
				diferencaHoras--;
				diferencaMinutos = (minutosTotalDia + 60) - minutosPrevisto;
			}
		} else if (horasTotalDia < horasPrevisto) {

			diferencaHoras = horasPrevisto - horasTotalDia;

			if (minutosTotalDia > minutosPrevisto) {
				diferencaHoras--;
				diferencaMinutos = (minutosPrevisto + 60) - minutosTotalDia;
			} else if (minutosTotalDia < minutosPrevisto) {
				diferencaMinutos = minutosPrevisto - minutosTotalDia;
			}
		} else {
			if (minutosTotalDia > minutosPrevisto) {
				diferencaMinutos = minutosTotalDia - minutosPrevisto;
			} else if (minutosTotalDia < minutosPrevisto) {
				diferencaMinutos = minutosPrevisto - minutosTotalDia;
			}
		}

		resultado = sinal + ManipulacaoUtil.adicionarChar('0', digitosHora, String.valueOf(diferencaHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaMinutos), true);

		return resultado;
	}

	/**
	 * @author Senio Caires
	 * @param saldoAcumulado
	 * @param saldoDia
	 * @return {@link String}
	 */
	public static String acumulaSaldoDia(String saldoAcumulado, String saldoDia) {

		String resultado = "000:00";

		int horasSaldoAcumulado = Integer.valueOf(saldoAcumulado.split(":")[0].replace("-", "").replace("+", "").trim());
		int minutosSaldoAcumulado = Integer.valueOf(saldoAcumulado.split(":")[1]);
		int horasSaldoDia = Integer.valueOf(saldoDia.split(":")[0].replace("-", "").replace("+", "").trim());
		int minutosSaldoDia = Integer.valueOf(saldoDia.split(":")[1]);
		int diferencaHoras = 0;
		int diferencaMinutos = 0;
		String sinal = "";

		if (saldoDia.contains("+")) {

			if (saldoAcumulado.contains("+")) {
				diferencaHoras = horasSaldoAcumulado + horasSaldoDia + Integer.valueOf((minutosSaldoAcumulado + minutosSaldoDia) / 60);
				diferencaMinutos = ((minutosSaldoAcumulado + minutosSaldoDia) % 60);
				sinal = "+";
			} else if (saldoAcumulado.contains("-")) {

				if (horasSaldoAcumulado < horasSaldoDia) {

					diferencaHoras = horasSaldoDia - horasSaldoAcumulado;

					if (minutosSaldoAcumulado > minutosSaldoDia) {
						diferencaHoras--;
						diferencaMinutos = (minutosSaldoDia + 60) - minutosSaldoAcumulado;
					} else if (minutosSaldoAcumulado < minutosSaldoDia) {
						diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
					}

					sinal = "+";
				} else if (horasSaldoAcumulado > horasSaldoDia) {

					diferencaHoras = horasSaldoAcumulado - horasSaldoDia;

					if (minutosSaldoAcumulado > minutosSaldoDia) {
						diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
					} else if (minutosSaldoAcumulado < minutosSaldoDia) {
						diferencaHoras--;
						diferencaMinutos = (minutosSaldoAcumulado + 60) - minutosSaldoDia;
					}

					sinal = "-";
				} else {

					if (minutosSaldoAcumulado > minutosSaldoDia) {
						diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
						sinal = "-";
					} else if (minutosSaldoAcumulado < minutosSaldoDia) {
						diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
						sinal = "+";
					} else {
						sinal = " ";
					}
				}
			} else {
				diferencaHoras = horasSaldoDia;
				diferencaMinutos = minutosSaldoDia;
				sinal = "+";
			}
		} else if (saldoDia.contains("-")) {

			if (saldoAcumulado.contains("+")) {

				if (horasSaldoAcumulado > horasSaldoDia) {

					diferencaHoras = horasSaldoAcumulado - horasSaldoDia;

					if (minutosSaldoAcumulado > minutosSaldoDia) {
						diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
						sinal = "+";
					} else if (minutosSaldoAcumulado < minutosSaldoDia) {
						diferencaHoras--;
						diferencaMinutos = (minutosSaldoAcumulado + 60) - minutosSaldoDia;
						sinal = "+";
					} else {
						diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
						sinal = " ";
					}
				} else if (horasSaldoAcumulado < horasSaldoDia) {

					diferencaHoras = horasSaldoDia - horasSaldoAcumulado;

					if (minutosSaldoAcumulado > minutosSaldoDia) {
						diferencaHoras--;
						diferencaMinutos = (minutosSaldoDia + 60) - minutosSaldoAcumulado;
					} else if (minutosSaldoAcumulado < minutosSaldoDia) {
						diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
					}

					sinal = "-";
				} else {

					if (minutosSaldoAcumulado > minutosSaldoDia) {
						diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
						sinal = "+";
					} else if (minutosSaldoAcumulado < minutosSaldoDia) {
						diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
						sinal = "-";
					} else {
						sinal = " ";
					}
				}
			} else if (saldoAcumulado.contains("-")) {
				diferencaHoras = horasSaldoAcumulado + horasSaldoDia + Integer.valueOf((minutosSaldoAcumulado + minutosSaldoDia) / 60);
				diferencaMinutos = ((minutosSaldoAcumulado + minutosSaldoDia) % 60);
				sinal = "-";
			} else {

				diferencaHoras = horasSaldoDia;
				diferencaMinutos = minutosSaldoDia;

				if (saldoDia.contains("+")) {
					sinal = "+";
				} else if (saldoDia.contains("-")) {
					sinal = "-";
				} else {
					sinal = " ";
				}
			}
		} else {

			diferencaHoras = horasSaldoAcumulado;
			diferencaMinutos = minutosSaldoAcumulado;

			if (saldoAcumulado.contains("+")) {
				sinal = "+";
			} else if (saldoAcumulado.contains("-")) {
				sinal = "-";
			} else {
				sinal = " ";
			}
		}

		resultado = sinal + ManipulacaoUtil.adicionarChar('0', 3, String.valueOf(diferencaHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaMinutos), true);

		return resultado;
	}

	/**
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public static final String getTotalHorasPrevistas() {

		String resultado = "00:00";

		for (Previsto previsto : ConfiguracaoUtil.getPrevistos()) {
			resultado = somaHorarios(resultado, previsto.getHoras(), 3);
		}

		return ManipulacaoUtil.adicionarChar('0', 6, String.valueOf(resultado), true);
	}

	/**
	 * @author Senio Caires
	 * @param horaInicial
	 * @param horaFinal
	 * @param digitosHora
	 * @return {@link String}
	 */
	public static final String somaHorarios(String horaInicial, String horaFinal, int digitosHora) {

		int totalHoras = Integer.valueOf(horaInicial.split(":")[0]) + Integer.valueOf(horaFinal.split(":")[0]) + Integer.valueOf((Integer.valueOf(horaInicial.split(":")[1]) + Integer.valueOf(horaFinal.split(":")[1])) / 60);
		int totalMinutos = (Integer.valueOf(horaInicial.split(":")[1]) + Integer.valueOf(horaFinal.split(":")[1])) % 60;

		return ManipulacaoUtil.adicionarChar('0', digitosHora, String.valueOf(totalHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(totalMinutos), true);
	}

	/**
	 * Retorna o path relativo.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	private static final String getPath() {
		Path pathRelativo = Paths.get("");
		return pathRelativo.toAbsolutePath().toString();
	}

	/**
	 * Ativa a conexão com o proxy.
	 * @author Senio Caires
	 */
	private static final void ativarConexaoProxy() {

		System.setProperty("http.proxyHost", ConfiguracaoUtil.getConfiguracaoProxy().getHost());
		System.setProperty("http.proxyPort", ConfiguracaoUtil.getConfiguracaoProxy().getPorta());
		System.setProperty("http.proxyUser", ConfiguracaoUtil.getConfiguracaoProxy().getLogin());
		System.setProperty("http.proxyPassword", ConfiguracaoUtil.getConfiguracaoProxy().getSenha());

		java.net.Authenticator.setDefault(
			new java.net.Authenticator() {
				public java.net.PasswordAuthentication getPasswordAuthentication() {
					return new java.net.PasswordAuthentication(ConfiguracaoUtil.getConfiguracaoProxy().getLogin(), ConfiguracaoUtil.getConfiguracaoProxy().getSenha().toCharArray());
				}
			}
		);
	}

	/**
	 * Método para acessar o Zeus.
	 * @author Senio Caires
	 * @return {@link Boolean}
	 */
	private static final Boolean acessarZeus() {

		Boolean acessoSucesso = Boolean.FALSE;
		WebClient webClient;

		if (ConfiguracaoUtil.getConfiguracaoProxy().getAtiva()) {

			webClient = new WebClient(BrowserVersion.CHROME, ConfiguracaoUtil.getConfiguracaoProxy().getHost(), Integer.valueOf(ConfiguracaoUtil.getConfiguracaoProxy().getPorta()));

			final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
			credentialsProvider.addCredentials(ConfiguracaoUtil.getConfiguracaoProxy().getLogin(), ConfiguracaoUtil.getConfiguracaoProxy().getSenha());

		} else {
			webClient = new WebClient(BrowserVersion.CHROME);
		}

		try {

			final HtmlPage paginaLogin = webClient.getPage("http://sistemas.pdcase.com/zeusprod21/hacessarsistema.aspx");

			final HtmlTextInput campoLogin = (HtmlTextInput) paginaLogin.getElementById("_PESLOGIN");
			final HtmlPasswordInput campoSenha = (HtmlPasswordInput) paginaLogin.getElementById("_PESSENHA");
			final HtmlSubmitInput botaoConfirmar = (HtmlSubmitInput) paginaLogin.getElementByName("BUTTON1");

			campoLogin.setValueAttribute(ConfiguracaoUtil.getUsuarioZeus().getLogin());
			campoSenha.setValueAttribute(ConfiguracaoUtil.getUsuarioZeus().getSenha());

			final HtmlPage paginaInicial = (HtmlPage) botaoConfirmar.click();

			if (!paginaInicial.asText().contains("Principal")) {
				webClient.close();
				throw new IOException();
			}

			final HtmlPage paginaRegistroPonto = webClient.getPage("http://sistemas.pdcase.com/zeusprod21/HRegistroPonto2.aspx");
			final HtmlSubmitInput botaoImprimir = (HtmlSubmitInput) paginaRegistroPonto.getElementByName("BUTTON2");

			UnexpectedPage paginaRelatorio = botaoImprimir.click();
			ConfiguracaoUtil.setUrlRelatorio(paginaRelatorio.getWebResponse().getWebRequest().getUrl().toString());

			acessoSucesso = Boolean.TRUE;
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Erro ao acessar Zeus.");
			return Boolean.FALSE;
		} catch (MalformedURLException e) {
			logger.error("Erro ao acessar Zeus.");
			return Boolean.FALSE;
		} catch (IOException e) {
			logger.error("Erro ao acessar Zeus.");
			return Boolean.FALSE;
		}

		webClient.close();

		return acessoSucesso;
	}
}
