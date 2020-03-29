package arduinoosc;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Ahmed
 */
public class FXMLDocumentController implements Initializable {
    
    Arduino arduino;
    
    @FXML
    private Button btn_connect;
    
    @FXML
    private Button btn_on_off;
    
    @FXML
    private ComboBox<String> combo;
    
    @FXML
    LineChart<String,Number> line_char;
        
    
    
    final int WINDOW_SIZE = 10;
    private ScheduledExecutorService scheduledExecutorService;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (btn_connect.getText().toString().equals("Connect")) {
            String port = combo.getSelectionModel().getSelectedItem().toString();
            arduino = new Arduino(port, 9600);
            arduino.openConnection();
            btn_connect.setText("DisConnect");
            combo.setEditable(false);
            
        }else {
            arduino.closeConnection();
            btn_connect.setText("Connect");
            combo.setEditable(true);
        }
    }
    
    
    @FXML
    private void btnONOFF(ActionEvent event) {
        if (btn_on_off.getText().toString().equals("ON")) {
            arduino.serialWrite("1");
            btn_on_off.setText("OFF");
        }else {
            arduino.serialWrite("0");
            btn_on_off.setText("ON");
        }
    }
    
    @FXML
    private void btnStartPlot(ActionEvent event) throws InterruptedException {
   
        XYChart.Series series = new XYChart.Series();
        /*int i = 0;
        while (i < 50) {
            String s = arduino.serialRead(0);
            System.out.println("String : " + s);
            float f = Float.parseFloat(s) * 10;
            if(f > 10){
                continue;
            }
            System.out.println("Float : " + f);
            //Thread.sleep(10);
            //double y = Float.valueOf(s) * 100;
            //int yi = Math.round(y);
            
            series.getData().add(new XYChart.Data(i, f));
            i++;
        }
        line_char.getData().add(series);*/
     
        // add series to chart
        line_char.getData().add(series);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            String s = arduino.serialRead(0);
            System.out.println("String : " + s);
            float f = Float.parseFloat(s) * 10;
            
            System.out.println("FLOAT : " + f);
            //Integer random = ThreadLocalRandom.current().nextInt(10);
            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), f));

                if (series.getData().size() > WINDOW_SIZE)
                    series.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SerialPort[] portNames = SerialPort.getCommPorts();
        for(int i = 0; i < portNames.length; i++)
            combo.getItems().add(portNames[i].getSystemPortName());
    
        
    }
    
}
