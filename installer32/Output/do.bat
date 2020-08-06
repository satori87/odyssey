makecert.exe ^
-n "CN=Odyssey" ^
-r ^
-pe ^
-a sha512 ^
-len 4096 ^
-cy authority ^
-sv Odyssey.pvk ^
Odyssey.cer

pvk2pfx.exe ^
-pvk Odyssey.pvk ^
-spc Odyssey.cer ^
-pfx Odyssey.pfx ^
-po kijlock22