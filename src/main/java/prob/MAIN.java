package prob;

import java.sql.*;
import java.util.Scanner;

import static java.lang.System.exit;

public class MAIN
{
    private static final String URL="jdbc:mysql://localhost:3306/lab8";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";


    private static void adaugaPersoana(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("Nume: ");
        String nume = scanner.nextLine();

        System.out.print("Varsta: ");
        try
        {
            String input = scanner.nextLine();
            int varsta = Integer.parseInt(input);
            if (varsta < 0 || varsta > 120)
            {
                throw new ExcepiteVarsta("Varsta trebuie sa fie intre 0 si 120.");
            }

            String sql = "INSERT INTO persoane (nume, varsta) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql))
            {
                stmt.setString(1, nume);
                stmt.setInt(2, varsta);
                stmt.executeUpdate();
                System.out.println("Persoana adaugata cu succes.");
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Eroare: Trebuie introdus un numar pentru varsta.");
        }
        catch (ExcepiteVarsta e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void adaugaExcursie(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("ID persoana: ");
        int idPersoana = Integer.parseInt(scanner.nextLine());

        System.out.print("Destinatia: ");
        String destinatia = scanner.nextLine();

        System.out.print("Anul excursiei: ");
        try
        {
            String inputAn = scanner.nextLine();
            int anul = Integer.parseInt(inputAn);

            String sqlPersoana = "SELECT varsta FROM persoane WHERE id = ?";
            try (PreparedStatement stmtPersoana = connection.prepareStatement(sqlPersoana))
            {
                stmtPersoana.setInt(1, idPersoana);
                ResultSet rs = stmtPersoana.executeQuery();

                if (rs.next())
                {
                    int varsta = rs.getInt("varsta");
                    int anulNasterii = 2024 - varsta;

                    if (anul < anulNasterii || anul > 2050)
                    {
                        throw new ExceptieAnExcursie("Anul excursiei trebuie sa fie intre anul nasterii ("
                                + anulNasterii + ") si 2050.");
                    }
                    String sqlExcursie = "INSERT INTO excursii (id_persoana, destinatia, anul) VALUES (?, ?, ?)";
                    try (PreparedStatement stmtExcursie = connection.prepareStatement(sqlExcursie))
                    {
                        stmtExcursie.setInt(1, idPersoana);
                        stmtExcursie.setString(2, destinatia);
                        stmtExcursie.setInt(3, anul);
                        stmtExcursie.executeUpdate();
                        System.out.println("Excursia a fost adaugata cu succes.");
                    }
                }
                else
                {
                    System.out.println("Persoana cu acest ID nu exista.");
                }
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Eroare: Anul trebuie sa fie un numar valid.");
        }
        catch (ExceptieAnExcursie e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void afiseazaPersoaneSiExcursii(Connection connection) throws SQLException
    {
        String sql = "SELECT p.id, p.nume, p.varsta, e.destinatia, e.anul FROM persoane p " +
                "LEFT JOIN excursii e ON p.id = e.id_persoana";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                System.out.printf("ID: %d, Nume: %s, Varsta: %d, Destinatie: %s, Anul: %d\n",
                        rs.getInt("id"), rs.getString("nume"), rs.getInt("varsta"),
                        rs.getString("destinatia"), rs.getInt("anul"));
            }
        }
    }
    private static void afiseazaExcursiiPentruPersoana(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("Numele persoanei: ");
        String nume = scanner.nextLine();
        String sql = "SELECT e.destinatia, e.anul FROM persoane p " +
                "JOIN excursii e ON p.id = e.id_persoana WHERE p.nume = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, nume);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                System.out.printf("Destinatie: %s, Anul: %d\n",
                        rs.getString("destinatia"), rs.getInt("anul"));
            }
        }
    }
    private static void afiseazaPersoanePentruDestinatie(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("Destinatia: ");
        String destinatia = scanner.nextLine();
        String sql = "SELECT DISTINCT p.nume, p.varsta FROM persoane p " +
                "JOIN excursii e ON p.id = e.id_persoana WHERE e.destinatia = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, destinatia);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                System.out.printf("Nume: %s, Varsta: %d\n", rs.getString("nume"), rs.getInt("varsta"));
            }
        }
    }
    private static void afiseazaPersoanePentruAn(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("Anul: ");
        int anul = Integer.parseInt(scanner.nextLine());
        String sql = "SELECT DISTINCT p.nume, p.varsta FROM persoane p " +
                "JOIN excursii e ON p.id = e.id_persoana WHERE e.anul = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, anul);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                System.out.printf("Nume: %s, Varsta: %d\n", rs.getString("nume"), rs.getInt("varsta"));
            }
        }
    }
    private static void stergeExcursie(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("ID excursie: ");
        int idExcursie = Integer.parseInt(scanner.nextLine());
        String sql = "DELETE FROM excursii WHERE id_excursie = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, idExcursie);
            int rows = stmt.executeUpdate();
            if (rows > 0)
            {
                System.out.println("Excursia a fost stearsa cu succes.");
            }
            else
            {
                System.out.println("Nu exista excursie cu acest ID.");
            }
        }
    }
    private static void stergePersoana(Connection connection, Scanner scanner) throws SQLException
    {
        System.out.print("ID persoana: ");
        int idPersoana = Integer.parseInt(scanner.nextLine());
        String sqlExcursii = "DELETE FROM excursii WHERE id_persoana = ?";
        try (PreparedStatement stmtExcursii = connection.prepareStatement(sqlExcursii))
        {
            stmtExcursii.setInt(1, idPersoana);
            stmtExcursii.executeUpdate();
        }
        String sqlPersoana = "DELETE FROM persoane WHERE id = ?";
        try (PreparedStatement stmtPersoana = connection.prepareStatement(sqlPersoana))
        {
            stmtPersoana.setInt(1, idPersoana);
            int rows = stmtPersoana.executeUpdate();
            if (rows > 0)
            {
                System.out.println("Persoana a fost stearsa cu succes.");
            }
            else
            {
                System.out.println("Nu exista persoana cu acest ID.");
            }
        }
    }











    public static void main(String[] args)
    {
        try(Connection connection= DriverManager.getConnection(URL,USER,PASSWORD))
        {
            Scanner scanner=new Scanner(System.in);
            int opt;
            do
            {
                System.out.println("\nMeniu:");
                System.out.println("1. Adaugare persoana");
                System.out.println("2. Adaugare excursie");
                System.out.println("3. Afisare persoane si excursii");
                System.out.println("4. Afisare excursii pentru o persoana");
                System.out.println("5. Persoane care au vizitat o destinatie");
                System.out.println("6. Persoane care au facut excursii intr-un an");
                System.out.println("7. Stergere excursie");
                System.out.println("8. Stergere persoana");
                System.out.println("0. Iesire");
                System.out.print("Alegeti optiunea: ");
                opt=scanner.nextInt();
                scanner.nextLine();
                switch(opt)
                {
                    case 1:adaugaPersoana(connection,scanner);break;
                    case 2:adaugaExcursie(connection,scanner);break;
                    case 3:afiseazaPersoaneSiExcursii(connection);break;
                    case 4:afiseazaExcursiiPentruPersoana(connection,scanner);break;
                    case 5:afiseazaPersoanePentruDestinatie(connection,scanner);break;
                    case 6:afiseazaPersoanePentruAn(connection,scanner);break;
                    case 7:stergeExcursie(connection,scanner);break;
                    case 8:stergePersoana(connection,scanner);break;
                    case 0:exit(0);break;

                }
            }
            while(opt!=0);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}
