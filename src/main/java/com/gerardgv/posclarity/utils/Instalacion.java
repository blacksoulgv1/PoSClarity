package com.gerardgv.posclarity.utils;

import com.gerardgv.posclarity.database.DBConnection;
import java.sql.*;

public class Instalacion {
    
    public static boolean existeSucursal() {
    
        String sql = "SELECT COUNT(*)FROM sucursal";
    
            try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            
                if(rs.next()){
                return rs.getInt(1) > 0;
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        return false;
    }
    
}
