init
// Programa com if
	k, m, result is integer;
	a, j is string;

	read(j);
	read(k);

 	if (j = "ok")
	begin
		result := k/m;
	end
	else
	begin
		result := 0;
		write("Invalid entry");
	end;

	write(result);
stop
