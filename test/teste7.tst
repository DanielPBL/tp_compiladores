init
 	a is int;

	begin
		read (A);

		DO
			A := A - 2
		WHiLE (A >= 2);

		iF (a = 0)
			write (A);
			write (" é par");
		ELSE
			write (A);
			write (" é ímpar.");
