init
	n is integer;
	anterior, proximo, aux, i is integer;

	begin
		write ("Digite a posicao: ");
		read (n);

		if ( n == 1)
			proximo := 0;
		else
			if ( n == 2)
				proximo := 1;
			else
				anterior := 0;
				proximo := 1
				i := 3;
				do
					aux := proximo;
					proximo := anterior + proximo;
					anterior := aux;
					i := i + 1;
				while (i < n)

    	write ("O termo: ");
		write (proximo);
stop
