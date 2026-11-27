import java.util.*;

public class Servidor
{
    public static String PORTA_PADRAO = "3000";

    public static void main (String[] args)
    {
        if (args.length>1)
        {
            System.err.println ("Uso esperado: java Servidor [PORTA]\n");
            return;
        }

        String porta=Servidor.PORTA_PADRAO;

        if (args.length==1)
            porta = args[0];

        ArrayList<Parceiro> usuarios =
        new ArrayList<Parceiro> ();

        AceitadoraDeConexao aceitadoraDeConexao=null;
        try
        {
            aceitadoraDeConexao =
            new AceitadoraDeConexao (porta, usuarios);
            aceitadoraDeConexao.start();
        }
        catch (Exception erro)
        {
            System.err.println ("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        System.out.println ("O servidor esta ativo na porta "+porta+"!");
        System.out.println ("Processadores nesta maquina: "+Runtime.getRuntime().availableProcessors());

        for(;;)
        {
            System.out.println ("Para desativa-lo, use o comando \"desativar\"\n");
            System.out.print   ("> ");

            String comando=null;
            try
            {
                comando = Teclado.getUmString();
            }
            catch (Exception erro)
            {}

            if (comando==null) // entrada do teclado encerrada (ex.: Ctrl+D)
            {
                System.err.println ("Entrada do teclado encerrada; o servidor continua ativo.");
                System.err.println ("Para desativa-lo agora, use Ctrl+C.\n");
                return; // so a main termina; a aceitadora continua atendendo
            }

            if (comando.trim().toLowerCase().equals("desativar"))
            {
                synchronized (usuarios)
                {
                    ComunicadoDeDesligamento comunicadoDeDesligamento =
                    new ComunicadoDeDesligamento ();

                    for (Parceiro usuario:usuarios)
                    {
                        try
                        {
                            usuario.receba (comunicadoDeDesligamento);
                            usuario.adeus  ();
                        }
                        catch (Exception erro)
                        {}
                    }
                }

                System.out.println ("O servidor foi desativado!\n");
                System.exit(0);
            }
            else
                System.err.println ("Comando invalido!\n");
        }
    }
}
