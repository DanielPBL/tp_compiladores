init
// Programa com if
	k, m, result is integer;
	a, j is string;

	read(j);
	read(k);
	m := 1;
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
