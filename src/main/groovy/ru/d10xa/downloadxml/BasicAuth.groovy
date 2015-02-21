package ru.d10xa.downloadxml

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ['username','password'])
class BasicAuth {
    private String username
    private String password

    BasicAuth(String username, password) {
        this.username = username
        this.password = password
    }

    public void fillRequestProperty(URLConnection connection){
        def authString = "$username:$password".getBytes().encodeBase64().toString()
        connection.setRequestProperty('Authorization',"Basic ${authString}")
    }

}
