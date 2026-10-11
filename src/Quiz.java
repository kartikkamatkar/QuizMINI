
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Quiz extends JFrame {

    ArrayList<String[]> question = new ArrayList<>();
    int curr = 0;
    int score = 0;

    JLabel questionLabel;
    JRadioButton a, b, c, d;
    ButtonGroup option;
    JButton nextButton;

    public Quiz() {
        setTitle("Quiz Management System - Quiz");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        a = new JRadioButton();
        b = new JRadioButton();
        c = new JRadioButton();
        d = new JRadioButton();

        Font optionFont = new Font("Arial", Font.PLAIN, 14);
        a.setFont(optionFont);
        b.setFont(optionFont);
        c.setFont(optionFont);
        d.setFont(optionFont);

        option = new ButtonGroup();
        option.add(a);
        option.add(b);
        option.add(c);
        option.add(d);

        nextButton = new JButton("Next");
        nextButton.setPreferredSize(new Dimension(100, 35));
        nextButton.addActionListener(e -> nextQuestion());

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 8));
        optionsPanel.add(a);
        optionsPanel.add(b);
        optionsPanel.add(c);
        optionsPanel.add(d);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(nextButton);

        panel.add(questionPanel, BorderLayout.NORTH);
        panel.add(optionsPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        loadQuestion();

        if (question.isEmpty()) {
            questionLabel.setText("No questions found.");
            nextButton.setEnabled(false);
        } else {
            showQuestion();
        }

        setVisible(true);
    }

    private void loadQuestion() {
        String sql = "SELECT question, option_a, option_b, option_c, " +
                "option_d, correct_answer FROM questions ORDER BY id";

        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                question.add(new String[]{
                        rs.getString("question"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Unable to load questions: " + e.getMessage());
        }
    }

    private void showQuestion() {
        String[] q = question.get(curr);

        questionLabel.setText(
                "<html><div style='width:500px'>Question " +
                        (curr + 1) + ": " + q[0] + "</div></html>");

        a.setText("A. " + q[1]);
        b.setText("B. " + q[2]);
        c.setText("C. " + q[3]);
        d.setText("D. " + q[4]);

        option.clearSelection();

        nextButton.setText(
                curr == question.size() - 1 ? "Submit" : "Next");
    }

    private void nextQuestion() {
        if (!a.isSelected() && !b.isSelected()
                && !c.isSelected() && !d.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please select an answer.");
            return;
        }

        String selected = "";

        if (a.isSelected()) selected = "A";
        else if (b.isSelected()) selected = "B";
        else if (c.isSelected()) selected = "C";
        else if (d.isSelected()) selected = "D";

        if (selected.equalsIgnoreCase(question.get(curr)[5].trim())) {
            score++;
        }

        if (curr < question.size() - 1) {
            curr++;
            showQuestion();
        } else {
            double percentage = score * 100.0 / question.size();

            JOptionPane.showMessageDialog(
                    this,
                    "Quiz completed!\nScore: " + score + " / " +
                            question.size() +
                            "\nPercentage: " +
                            String.format("%.1f%%", percentage));

            dispose();
        }
    }
}
