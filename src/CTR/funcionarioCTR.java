/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CTR;

import dao.FuncionariosDAO;
import hash.Hash;
import java.math.BigInteger;
import model.Funcionarios;
 
/**
 *
 * @author Rafael
 */
public class funcionarioCTR {
    public void CadastrarFuncionarioCTR(String email,String senha){
        Funcionarios objM = new Funcionarios();
        objM.setEmail(email);
        objM.setSenha(Hash.converter(senha));
        
        FuncionariosDAO objD = new FuncionariosDAO();
        objD.cadastrarFuncionario(objM);
    }
    public void LogarFuncionarioCTR(String email,String senha){
       Funcionarios objM = new Funcionarios();
       objM.setEmail(email);
       objM.setSenha(Hash.converter(senha));
    
       FuncionariosDAO objD = new FuncionariosDAO();
       objD.logar(objM);
    }

}
