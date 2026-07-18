package com.gerardgv.posclarity.database;

import com.gerardgv.posclarity.models.Empleados;
import com.gerardgv.posclarity.utils.PasswordUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadosDAO {
    
    public boolean insert(Empleados empleado){
        
        if (empleado == null
            || empleado.getPasswordHash() == null
            || empleado.getPasswordHash().isBlank()) {
        return false;
    }
        
        String sql ="""
                    INSERT INTO vendedor
                    (nombre,codigo,rol,passwordHash,estatus) 
                    VALUES (?,?,?,?,?)
                    """;
        
        try( Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1,empleado.getNombre());
            stm.setInt(2, empleado.getCodigo());
            stm.setString(3,empleado.getRol());
            stm.setString(4, empleado.getPasswordHash());            
            stm.setBoolean(5, empleado.isActivo());
            
            return stm.executeUpdate()>0;
            
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean update(Empleados empleado){
        
        if (empleado == null || empleado.getId_vendedor() <= 0) {
            return false;
        }
        
        boolean cambiarPassword = empleado.getPasswordHash() != null
                && !empleado.getPasswordHash().isBlank();
        
        String sql = cambiarPassword ? """
                    UPDATE vendedor 
                    SET nombre = ?, codigo = ?, passwordHash = ?, rol = ?, estatus = ? 
                    WHERE id_vendedor=?
                    """ :
                """
                 UPDATE vendedor
                              SET nombre = ?,
                                  codigo = ?,
                                  rol = ?,
                                  estatus = ?
                              WHERE id_vendedor = ?
                """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, empleado.getNombre());
            stm.setInt(2, empleado.getCodigo());
            
            if(cambiarPassword){
                
                stm.setString(3, empleado.getPasswordHash());
                stm.setString(4, empleado.getRol());
                stm.setBoolean(5, empleado.isActivo());
                stm.setInt(6, empleado.getId_vendedor());
                
            } else {
                stm.setString(3, empleado.getRol());
                stm.setBoolean(4, empleado.isActivo());
                stm.setInt(5, empleado.getId_vendedor());
            }
            
            return stm.executeUpdate()>0;                       
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cambiarStatus(int id, boolean estado){
        
        String sql = "UPDATE vendedor SET estatus=? WHERE id_vendedor=?";
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setBoolean(1, estado);
            stm.setInt(2, id);
            return  stm.executeUpdate()>0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Empleados> findAll(){
        
        List<Empleados> lista = new ArrayList<>();
        
        String sql =  """
                SELECT id_vendedor,
                    nombre,
                    codigo,
                    passwordHash,
                    rol,
                    estatus
                FROM vendedor
                ORDER BY nombre
                """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                 lista.add(mapEmpleado(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Empleados> buscarPorNombre(String nombre){
        
        List<Empleados> lista = new ArrayList<>();
        
        String sql =  """
        SELECT id_vendedor,
               nombre,
               codigo,
               passwordHash,
               rol,
               estatus
        FROM vendedor
        WHERE nombre LIKE ?
          AND estatus = true
        ORDER BY nombre
        """;
        
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)){
            
            stm.setString(1, "%" + nombre + "%");
            
            try(ResultSet rs = stm.executeQuery()){
                while(rs.next()){                
                     lista.add(mapEmpleado(rs));          
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    
    public Empleados buscarPorCodigo(int codigo){
            
            String sql = """
                    SELECT id_vendedor,
                    nombre,
                    codigo,
                    passwordHash,
                    rol,
                    estatus
                    FROM vendedor
                    WHERE codigo = ?
                    LIMIT 1
                    """;

            try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, codigo);

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (rs.next()) {
                        return mapEmpleado(rs);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
        }
        return null;
    }
    
    public Empleados validarAcceso( int codigo, String password) {

        Empleados empleado = buscarPorCodigo(codigo);

        if (empleado == null) {
            return null;
        }

        if (!empleado.isActivo()) {
            return null;
        }

        if (!PasswordUtils.matches(
            password,
            empleado.getPasswordHash())) {

            return null;
        }
        return empleado;
    }
    
    private Empleados mapEmpleado(ResultSet rs) throws SQLException {

    Empleados empleado = new Empleados();

    empleado.setId_vendedor(rs.getInt("id_vendedor"));
    empleado.setNombre(rs.getString("nombre"));
    empleado.setCodigo(rs.getInt("codigo"));
    empleado.setPasswordHash(rs.getString("passwordHash"));
    empleado.setRol(rs.getString("rol"));
    empleado.setActivo(rs.getBoolean("estatus"));

    return empleado;
    }
    
    public boolean existeCodigo( int codigo, Integer idExcluir) {

        String sql = """
            SELECT COUNT(*)
            FROM vendedor
            WHERE codigo = ?
            AND (? IS NULL OR id_vendedor <> ?)
            """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigo);

            if (idExcluir == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, idExcluir);
                stmt.setInt(3, idExcluir);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
    
}
