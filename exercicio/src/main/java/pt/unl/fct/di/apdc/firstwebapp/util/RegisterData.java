package pt.unl.fct.di.apdc.firstwebapp.util;


public class RegisterData {

	public String username;
	public String password;
	public String email;
	public String name;
	public String role;
	public String state;
	public String profile;
	public String phoneNumber;
	public String mobilePhone;
	public String occupation;
	public String workplace;
	public String address;
	public String compAddress;
	public String nif;
	

	public RegisterData() {
	}

	public RegisterData(String username, String password, String email, String name, String profile, String phoneNumber,
			String mobilePhone, String occupation, String workplace, String address, String compAdress, String nif) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
		this.profile = profile;
		this.phoneNumber = phoneNumber;
		this.mobilePhone = mobilePhone;
		this.occupation = occupation;
		this.workplace = workplace;
		this.address = address;
		this.compAddress = compAdress;
		this.nif = nif;
	}

	public boolean validRegistration() {
		if (username == null || password == null || email == null || name == null) {
			return false; // retorna falso se algum dos campos não estiver preenchido
		}
		// verifica se o email está no formato correto
		/**
		 * ^ - indica o início da linha; [\\w-.]+ - indica que o email começa com um ou
		 * mais caracteres alfanuméricos, pontos ou hifens; @ - indica a presença do
		 * símbolo @ no email; ([\\w-]+\\.)+ - indica que, após o @, deve haver uma
		 * sequência de um ou mais caracteres alfanuméricos ou hifens, seguida de um
		 * ponto. Essa sequência pode-se repetir várias vezes (por isso o + fora dos
		 * parênteses), o que permite múltiplos subdomínios no email, como
		 * john.doe@subdominio.empresa.com; [\\w-]{2,4} - indica que o email deve
		 * terminar com um domínio contendo de 2 a 4 caracteres alfanuméricos ou hifens.
		 * Esta parte da expressão limita os top-level domains a 2, 3 ou 4 caracteres,
		 * como .com, .org, .net, .edu, .gov, .info, .io, etc.; $ - indica o final da
		 * linha.
		 **/
		if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			return false;
		}
		return true;
	}
}
