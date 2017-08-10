/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstanceReaderWithMySQL;

import InstanceReader.ConnectionFactory;
import com.mysql.jdbc.Connection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author renansantos
 */
public class ConnectionFactoryTest {

    public ConnectionFactoryTest() {
        Connection conn = (Connection) new ConnectionFactory().getConnection();

    }

    @Test
    public void testGetConnection() {
        Connection conn = (Connection) new ConnectionFactory().getConnection();
        System.out.println(conn);
        assertEquals(null, conn);
    }

}
