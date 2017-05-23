init
 	a is integer;

		read (A);

		DO
			A := A - 2;
		WHiLE (A >= 2);

		iF (a = 0)
    begin
			write (A);
			write (" é par");
    end
		ELSE
    begin
			write (A);
			write (" é ímpar.");
    end;
stop
