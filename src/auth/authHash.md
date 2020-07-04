To generate sha256 use following command in mac

echo -n ktor10ktorHashed | openssl dgst -binary -sha256 | openssl base64

where ktor can be any static name,
10 is the length of words after 10 numeric here i.e. ktorHashed