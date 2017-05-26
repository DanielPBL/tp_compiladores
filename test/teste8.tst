init
	n is integer;
	anterior, proximo, aux, i is integer;

	write("Digite a posicao: ");
	read(n);

	if (n = 1)
	begin
		proximo := 0;
	end
	else
	begin
		if (n = 2)
		begin
			proximo := 1;
		end
		else
		begin
			anterior := 1;
			proximo := 1;
			i := 3;
			do
				aux := proximo;
				proximo := anterior + proximo;
				anterior := aux;
				i := i + 1;
			while (i < n);
		end;
	end;

	write("O termo: ");
	write(proximo);
stop
