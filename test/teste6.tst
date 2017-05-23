init
 	a, b, c, maior is integer;

		read(a);
		read(b);
		read(c);

		maior := 0;
		if ( a>b)
    begin
      if (a>c)
      begin

			   maior := a;
      end;
    end
		else
    begin
			if (b>c)
      begin
				maior := b;
      end
			else
      begin
				maior := c;
      end;
    end;

		write("Maior idade: ");
		write(maior);

stop
