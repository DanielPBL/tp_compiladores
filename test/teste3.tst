{ Programa de Teste
Calculo de idade }
init
	cont_ is integer;
	media, idade, soma, altura is integer;

	cont_ := 5;
	soma := 0;
	do
		write("Altura: ");
		read (altura);
		soma := soma + altura;
		cont_ := cont_ - 1;
	while (cont_ > 0);

	write("Media: ");
	write(soma / 5);
stop
