package org.example;

import org.example.model.lexical.Token;
import org.example.pattern.CodeGenerationVisitor;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("请输入文件路径");
        }

        String path = args[0];
        if (path.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }
        List<File> files;
        if (file.isDirectory()) {
            files = Arrays.stream(Objects.requireNonNull(file.listFiles((dir, name) -> name.endsWith(".jack"))))
                    .collect(Collectors.toList());
        } else {
            files = Collections.singletonList(file);
        }
        for (File srcFile : files) {
            String fileName = srcFile.getName().split("\\.")[0];
            try (BufferedReader br = new BufferedReader(new FileReader(srcFile))) {
                Tokenizer tokenizer = new Tokenizer(br);
                List<Token> tokens = tokenizer.tokenize();
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
                CodeGenerationVisitor visitor = new CodeGenerationVisitor();
                syntaxAnalyzer.parseFile(tokens, visitor);

                File writeTo = new File((file.isDirectory() ? file.getAbsolutePath() : file.getParent()) + "/" + fileName + ".vm");
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(writeTo))) {
                    bw.write(visitor.toString());
                    bw.flush();
                } catch (IOException e) {
                    throw new IllegalArgumentException("写入文件失败");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("文件读取失败");
            }
        }

    }
}