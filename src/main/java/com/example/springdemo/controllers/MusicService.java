package com.example.springdemo.controllers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.*;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;

import javax.imageio.ImageIO;

import javax.sound.sampled.*;
import java.io.*;
import java.util.List;
import java.util.Vector;

class Pair {
    int x;
    int y;

    // Constructor
    public Pair(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}



//class Compare {
//
//    static void compare(Pair arr[], int n)
//    {
//        // Comparator to sort the pair according to second element
//        Arrays.sort(arr, new Comparator<Pair>() {
//            @Override public int compare(Pair p1, Pair p2)
//            {
//                return p1.y - p2.y;
//            }
//        });
//
//        for (int i = 0; i < n; i++) {
//            System.out.print(arr[i].x + " " + arr[i].y + " ");
//        }
//        System.out.println();
//    }
//}




public class MusicService
{

    private int[] ff = new int[0];
    public  MusicService(String imageFile) throws IOException
    {
        //Pattern vocals = new Pattern();
        //        vocals.add("V0 C D E F G A B");
        //        vocals.add("V1 R R A A A A A");
        //
        //Player player = new Player();
        //player.play("A8");

        Color bgc = new Color(toBinaryCode(255, 255, 255, 255));
        processImage(imageFile, bgc, 10);
    }

    //颜色相关
    private  int toBinaryCode(int a, int r, int g, int b)
    {
        return ((a << 24) | (r << 16) | (g << 8) | b);
    }

    private  int sq(int x) {return x * x;}
    private  boolean isSimilar(Color cl1, Color cl2, int value)
    {
        int tmp = sq(cl1.getAlpha() - cl2.getAlpha()) + sq(cl1.getRed() - cl2.getRed()) +
                sq(cl1.getGreen() - cl2.getGreen()) + sq(cl1.getBlue() - cl2.getBlue());

        return tmp <= value;
    }

    //并查集
    private  int getF(int x)
    {
        if (ff[x] == x)
            return x;
        return ff[x] = getF(ff[x]);
    }
    private  void merge(int ind1, int ind2)
    {
        ff[getF(ind2)] = getF(ind1);
    }

    private  boolean isG(int x, int y, int w, int h)
    {
        return x >= 0 && x <= w - 1 && y >= 0 && y <= h - 1;
    }

    private  void processImage(String imageFile, Color _BGC, int similarValue) throws IOException
    {
        //int[] rgb = new int[3];
        File file = new File(imageFile);

        BufferedImage image = null;
        try
        {
            image = ImageIO.read(file);
        } catch (IOException e) {

            e.printStackTrace();
        }

        int iWidth = image.getWidth();
        int iHight = image.getHeight();
        int sttX = image.getMinX();
        int sttY = image.getMinY();

        ff = new int[iWidth * iHight];

        for(int i = 0; i <= iWidth * iHight - 1; ++i)
            ff[i] = i;

        int[] dx = {-1, 0, 0};
        int[] dy = {0, -1, 1};
        for(int x = sttX; x <= iWidth - 1; x++)
        {
            for(int y = sttY; y <= iHight - 1; y++)
            {
                int pixel = image.getRGB(x, y);
                int posCode = y * iWidth + x;
                Color nowC = new Color(pixel);

                for (int id = 0; id <= 2; ++id)
                {
                    int tmpX = x + dx[id], tmpY = y + dy[id], posCodeTmp = tmpY * iWidth + tmpX;

                    if (!isG(tmpX, tmpY, iWidth, iHight)) continue;

                    Color tmpC = new Color(image.getRGB(tmpX, tmpY));
                    if (isSimilar(nowC, tmpC, similarValue))
                    {
                        merge(posCode, posCodeTmp);
                    }
                }

                //int r = nowC.getRed();
                //int g = nowC.getGreen();
                //int b = nowC.getBlue();

                //System.out.print("("+ r + "," + g + "," + b + ") ");
            }
        }

        //Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        //map.put("dog", "type of animal");
        //System.out.println(map.get("dog"));

        Map<Integer, ArrayList<Integer>> mmap = new HashMap<>();

        for(int x = sttX; x <= iWidth - 1; x++)
        {
            for(int y = sttY; y <= iHight - 1; y++)
            {
                int pixel = image.getRGB(x, y);
                int posCode = y * iWidth + x;
                Color nowC = new Color(pixel);

                if (isSimilar(nowC, _BGC, similarValue)) continue;

                if (mmap.get(getF(posCode)) == null)
                {
                    ArrayList<Integer> tmp = new ArrayList<Integer>();
                    tmp.add(posCode);
                    mmap.put(getF(posCode), tmp);
                }
                else
                {
                    mmap.get(getF(posCode)).add(posCode);
                }
            }
        }

        ArrayList<ArrayList<Integer>> allLine = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> x:mmap.values()) allLine.add(x);


        //        ArrayList<ArrayList<Integer>> allLine = new ArrayList<ArrayList<Integer>>();
        //        for(int x = sttX; x <= iWidth - 1; x++)
        //        {
        //            for(int y = sttY; y <= iHight - 1; y++)
        //            {
        //                int pixel = image.getRGB(x, y);
        //                int posCode = y * iWidth + x;
        //                Color nowC = new Color(pixel);
        //
        //                if (isSimilar(nowC, _BGC, similarValue)) continue;
        //
        //                boolean flg = false;
        //                for (int i = 0; i <= allLine.size() - 1; ++i)
        //                {
        //                    if (getF(posCode) == getF(allLine.get(i).get(0)))
        //                    {
        //                        flg = true;
        //                        allLine.get(i).add(posCode);
        //                        break;
        //                    }
        //                }
        //
        //                if (!flg)
        //                {
        //                    ArrayList<Integer> tmp = new ArrayList<Integer>(); tmp.add(posCode);
        //                    allLine.add(tmp);
        //                }
        //            }
        //        }

        String popD = "CDEFGAB";
        ArrayList<String> pop = new ArrayList<String>();
        for (int i = 1; i <= 9; ++i)    //3--7
        {
            for (int j = 0; j <= 6; ++j)
            {
                Integer tmpI = i;
                String tmp = popD.charAt(j) + tmpI.toString();
                pop.add(tmp);
            }
        }

        int c = 0, sizP = pop.size();
        ArrayList<String> allTrack = new ArrayList<String>();
        for (ArrayList<Integer> arr:allLine)
        {
            //System.out.print("(" + pc % iWidth + ", " + pc / iWidth + ") ");
            int siz = arr.size();

            int tmpI = 0;
            Pair arrPs[] = new Pair[siz];
            for (int pc : arr)
            {
                arrPs[tmpI++] = new Pair(pc % iWidth, pc / iWidth);
            }

            Arrays.sort(arrPs, new Comparator<Pair>() {
                @Override public int compare(Pair p1, Pair p2)
                {
                    return p1.x - p2.x;
                }
            });

            ArrayList<Pair> noteTL = new ArrayList<Pair>(), noteTH = new ArrayList<Pair>();

            String trackH = "", trackL = "", allDuration = "siqhw";
            for (int i = 0; i <= siz - 1;)
            {
                Pair nowP = arrPs[i];
                int maxY = arrPs[i].y;

                while (i <= siz - 1)
                {
                    ++i;
                    if (i > siz - 1) break;
                    if (arrPs[i].x != nowP.x) break;
                    maxY = arrPs[i].y;
                }

                int minY = nowP.y;

                int nowNoteL = Math.min(sizP - 1, (minY / (iHight / sizP))), nowNoteH = Math.min(sizP - 1, (maxY / (iHight / sizP)));
                if (noteTL.size() > 0)
                {
                    if (noteTL.get(noteTL.size() - 1).x == nowNoteL && noteTL.get(noteTL.size() - 1).y < allDuration.length() - 1)
                    {
                        noteTL.set(noteTL.size() - 1, new Pair(nowNoteL, noteTL.get(noteTL.size() - 1).y + 1));
                    }
                    else
                    {
                        noteTL.add(new Pair(nowNoteL, 0));
                    }
                }
                else
                {
                    noteTL.add(new Pair(nowNoteL, 0));
                }

                if (noteTH.size() > 0)
                {
                    if (noteTH.get(noteTH.size() - 1).x == nowNoteH && noteTH.get(noteTH.size() - 1).y < allDuration.length() - 1)
                    {
                        noteTH.set(noteTH.size() - 1, new Pair(nowNoteH, noteTH.get(noteTH.size() - 1).y + 1));
                    }
                    else
                    {
                        noteTH.add(new Pair(nowNoteH, 0));
                    }
                }
                else
                {
                    noteTH.add(new Pair(nowNoteH, 0));
                }

                trackL += " " + pop.get(Math.min(sizP - 1, (minY / (iHight / sizP)))) + "s";
                trackH += " " + pop.get(Math.min(sizP - 1, (maxY / (iHight / sizP)))) + "s";
            }

            StringBuilder trackLB = new StringBuilder(trackL);
            trackLB.setCharAt(trackL.length() - 1, 'w');
            trackL = trackLB.toString();

            StringBuilder trackHB = new StringBuilder(trackH);
            trackHB.setCharAt(trackH.length() - 1, 'w');
            trackH = trackHB.toString();

            if (noteTL.size() > 0)
                noteTL.set(noteTL.size() - 1, new Pair(noteTL.get(noteTL.size() - 1).x, allDuration.length() - 1));
            if (noteTH.size() > 0)
                noteTH.set(noteTH.size() - 1, new Pair(noteTH.get(noteTH.size() - 1).x, allDuration.length() - 1));

            String tmpL = "", tmpH = "";
            for (Pair pr:noteTL)
                tmpL += " " + pop.get(pr.x) + allDuration.charAt(pr.y);
            for (Pair pr:noteTH)
                tmpH += " " + pop.get(pr.x) + allDuration.charAt(pr.y);


            //allTrack.add(trackH);
            //allTrack.add(trackL);

            allTrack.add(tmpH);
            allTrack.add(tmpL);
        }

        int totalSiz = 0;
        for (String str:allTrack) totalSiz += str.length();
        final double percent = 3e-5;

        ArrayList<String> newAllTrack = new ArrayList<String>();
        for (String str:allTrack)
        {
            if ((double)str.length() >= (double)totalSiz * percent) newAllTrack.add(str);
        }

        List<String> wavFileList=new ArrayList<>();
        Pattern song = new Pattern();

        int step = 4;
        for (int i = 0, siz = newAllTrack.size(); i <= siz - 1; i += step)
        {
            //Player pl = new Player();
            Pattern tmp = new Pattern();

            ArrayList<String> nowList = new ArrayList<String>();

            for (int j = 0; j <= step - 1; ++j)
            {
                if (i + j >= siz) break;
                nowList.add(newAllTrack.get(i + j));
            }

            int maxTrackL = 0;
            for (String str:nowList)
            {
                maxTrackL = Math.max(maxTrackL, str.length());
            }
            for (int k = 0; k <= nowList.size() - 1; ++k)
            {
                int orgSiz = nowList.get(k).length();
                String tmpS = nowList.get(k);
                for (int j = 0; tmpS.length() <= maxTrackL - 1; j = (j + 1) % orgSiz)
                {
                    tmpS += tmpS.charAt(j);
                }
                nowList.set(k, tmpS);
            }

            for (int j = 0; j <= nowList.size() - 1; ++j)
            {
                String sound;
                sound = "V" + j + nowList.get(j);
                tmp.add(sound);
                System.out.println(sound);
            }

            tmp.setTempo(180);
            tmp.setInstrument(0);

            //pl.play(tmp);

            song.add(tmp);
//            String midiFileName=file.getAbsolutePath().replaceAll("[.][^.]+$", "") + "-" + i+".midi";
//            File midiFile = new File(midiFileName);
//            MidiFileManager.savePatternToMidi(tmp, midiFile);
//            String wavF=MidiConvertToWav(midiFileName);
//            wavFileList.add(wavF);

        }

        String midiFileName=file.getAbsolutePath().replaceAll("[.][^.]+$", "") +".midi";
        File midiFile = new File(midiFileName);
        MidiFileManager.savePatternToMidi(song, midiFile);

        if(Files.exists(midiFile.toPath()))
            System.out.println("create file:" + midiFile +" is OK.");

        //String wavF=MidiConvertToWav(midiFileName);

        //wavFileList.add(wavF);

//        if(wavFileList.isEmpty()==false)
//            MergeToWav(wavFileList,file.getAbsolutePath());

    }

    private void MergeToWav(List<String> wavFileList,String imageFileName )
    {
        try
        {
            System.out.println("total:"+wavFileList.size());
            String wavFileName = imageFileName.substring(0,imageFileName.lastIndexOf('.')+1)+"wav";
            File fileOut = new File(wavFileName);

            AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(wavFileList.get(0)));
            if (wavFileList.size() >= 2)
            {
                //AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(midiFileList.get(0)));
                AudioInputStream audio2 = AudioSystem.getAudioInputStream(new File(wavFileList.get(1)));
                AudioInputStream audioBuild = new AudioInputStream(new SequenceInputStream(audio1, audio2), audio1.getFormat(), audio1.getFrameLength() + audio2.getFrameLength());

                System.out.println("0->1");

                //大于两个时继续合并
                for (int i = 2; i < wavFileList.size(); i++)
                {
                    System.out.println((i-1) + "->" +i);
                    AudioInputStream audio3 = AudioSystem.getAudioInputStream(new File(wavFileList.get(i)));
                    audioBuild = new AudioInputStream(new SequenceInputStream(audioBuild, audio3), audioBuild.getFormat(), audioBuild.getFrameLength() + audio3.getFrameLength());
                    //audio3.close();
                }
                //生成语音
                AudioSystem.write(audioBuild, AudioFileFormat.Type.WAVE, fileOut);

                audio2.close();
                audioBuild.close();

            }
            else
            {
                AudioSystem.write(audio1, AudioFileFormat.Type.WAVE, fileOut);
            }
            audio1.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private String MidiConvertToWav(String midiFileName)
    {
        try{
            System.out.println("转换中..."+midiFileName);

            String wavFileName = midiFileName.substring(0,midiFileName.lastIndexOf('.')+1)+"wav";

            //获取音频输入流
            AudioInputStream  audioStream = AudioSystem.getAudioInputStream(new File(midiFileName));
            try {
                var file=new File(wavFileName);
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
                audioStream.close();
                return file.getAbsolutePath();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}



class MusicServiceNew
{
    private int[] ff = new int[0];
    public MusicServiceNew(String imageFile) throws IOException
    {
        //Pattern vocals = new Pattern();
        //        vocals.add("V0 C D E F G A B");
        //        vocals.add("V1 R R A A A A A");
        //
        //Player player = new Player();
        //player.play("A8");

        Color bgc = new Color(toBinaryCode(255, 255, 255, 255));

        processImage(imageFile, bgc, 10);
    }

    //颜色相关
    private int toBinaryCode(int a, int r, int g, int b)
    {
        return ((a << 24) | (r << 16) | (g << 8) | b);
    }

    private static int sq(int x) {return x * x;}
    private static boolean isSimilar(Color cl1, Color cl2, int value)
    {
        int tmp = sq(cl1.getAlpha() - cl2.getAlpha()) + sq(cl1.getRed() - cl2.getRed()) +
                sq(cl1.getGreen() - cl2.getGreen()) + sq(cl1.getBlue() - cl2.getBlue());

        return tmp <= value;
    }

    //并查集
    private int getF(int x)
    {
        if (ff[x] == x)
            return x;
        return ff[x] = getF(ff[x]);
    }
    private void merge(int ind1, int ind2)
    {
        ff[getF(ind2)] = getF(ind1);
    }

    private boolean isG(int x, int y, int w, int h)
    {
        return x >= 0 && x <= w - 1 && y >= 0 && y <= h - 1;
    }

    private void processImage(String imageFile, Color _BGC, int similarValue) throws IOException
    {
        //int[] rgb = new int[3];
        File file = new File(imageFile);

        BufferedImage image = null;
        try
        {
            image = ImageIO.read(file);
        } catch (IOException e) {

            e.printStackTrace();
        }

        int iWidth = image.getWidth();
        int iHight = image.getHeight();
        int sttX = image.getMinX();
        int sttY = image.getMinY();

        ff = new int[iWidth * iHight];

        for(int i = 0; i <= iWidth * iHight - 1; ++i)
            ff[i] = i;

        int[] dx = {-1, 0, 0};
        int[] dy = {0, -1, 1};
        for(int x = sttX; x <= iWidth - 1; x++)
        {
            for(int y = sttY; y <= iHight - 1; y++)
            {
                int pixel = image.getRGB(x, y);
                int posCode = y * iWidth + x;
                Color nowC = new Color(pixel);

                for (int id = 0; id <= 2; ++id)
                {
                    int tmpX = x + dx[id], tmpY = y + dy[id], posCodeTmp = tmpY * iWidth + tmpX;

                    if (!isG(tmpX, tmpY, iWidth, iHight)) continue;

                    Color tmpC = new Color(image.getRGB(tmpX, tmpY));
                    if (isSimilar(nowC, tmpC, similarValue))
                    {
                        merge(posCode, posCodeTmp);
                    }
                }

                //int r = nowC.getRed();
                //int g = nowC.getGreen();
                //int b = nowC.getBlue();

                //System.out.print("("+ r + "," + g + "," + b + ") ");
            }
        }

        //Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        //map.put("dog", "type of animal");
        //System.out.println(map.get("dog"));

        Map<Integer, ArrayList<Integer>> mmap = new HashMap<>();

        for(int x = sttX; x <= iWidth - 1; x++)
        {
            for(int y = sttY; y <= iHight - 1; y++)
            {
                int pixel = image.getRGB(x, y);
                int posCode = y * iWidth + x;
                Color nowC = new Color(pixel);

                if (isSimilar(nowC, _BGC, similarValue)) continue;

                if (mmap.get(getF(posCode)) == null)
                {
                    ArrayList<Integer> tmp = new ArrayList<Integer>();
                    tmp.add(posCode);
                    mmap.put(getF(posCode), tmp);
                }
                else
                {
                    mmap.get(getF(posCode)).add(posCode);
                }
            }
        }

        ArrayList<ArrayList<Integer>> allLine = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> x:mmap.values()) allLine.add(x);


        //        ArrayList<ArrayList<Integer>> allLine = new ArrayList<ArrayList<Integer>>();
        //        for(int x = sttX; x <= iWidth - 1; x++)
        //        {
        //            for(int y = sttY; y <= iHight - 1; y++)
        //            {
        //                int pixel = image.getRGB(x, y);
        //                int posCode = y * iWidth + x;
        //                Color nowC = new Color(pixel);
        //
        //                if (isSimilar(nowC, _BGC, similarValue)) continue;
        //
        //                boolean flg = false;
        //                for (int i = 0; i <= allLine.size() - 1; ++i)
        //                {
        //                    if (getF(posCode) == getF(allLine.get(i).get(0)))
        //                    {
        //                        flg = true;
        //                        allLine.get(i).add(posCode);
        //                        break;
        //                    }
        //                }
        //
        //                if (!flg)
        //                {
        //                    ArrayList<Integer> tmp = new ArrayList<Integer>(); tmp.add(posCode);
        //                    allLine.add(tmp);
        //                }
        //            }
        //        }

        String popD = "CDEFGAB";
        ArrayList<String> pop = new ArrayList<String>();
        for (int i = 2; i <= 8; ++i)
        {
            for (int j = 0; j <= 6; ++j)
            {
                Integer tmpI = i;
                String tmp = popD.charAt(j) + tmpI.toString();
                pop.add(tmp);
            }
        }

        int c = 0, sizP = pop.size();
        ArrayList<String> allTrack = new ArrayList<String>();
        for (ArrayList<Integer> arr:allLine)
        {
            //System.out.print("(" + pc % iWidth + ", " + pc / iWidth + ") ");
            int siz = arr.size();

            int tmpI = 0;
            Pair arrPs[] = new Pair[siz];
            for (int pc : arr)
            {
                arrPs[tmpI++] = new Pair(pc % iWidth, pc / iWidth);
            }

            Arrays.sort(arrPs, new Comparator<Pair>() {
                @Override public int compare(Pair p1, Pair p2)
                {
                    return p1.x - p2.x;
                }
            });

            ArrayList<Pair> noteTL = new ArrayList<Pair>(), noteTH = new ArrayList<Pair>();

            String trackH = "", trackL = "", allDuration = "siqhw";
            for (int i = 0; i <= siz - 1;)
            {
                Pair nowP = arrPs[i];
                int maxY = arrPs[i].y;

                while (i <= siz - 1)
                {
                    ++i;
                    if (i > siz - 1) break;
                    if (arrPs[i].x != nowP.x) break;
                    maxY = arrPs[i].y;
                }

                int minY = nowP.y;

                int nowNoteL = Math.min(sizP - 1, (minY / (iHight / sizP))), nowNoteH = Math.min(sizP - 1, (maxY / (iHight / sizP)));
                if (noteTL.size() > 0)
                {
                    if (noteTL.get(noteTL.size() - 1).x == nowNoteL && noteTL.get(noteTL.size() - 1).y < allDuration.length() - 1)
                    {
                        noteTL.set(noteTL.size() - 1, new Pair(nowNoteL, noteTL.get(noteTL.size() - 1).y + 1));
                    }
                    else
                    {
                        noteTL.add(new Pair(nowNoteL, 0));
                    }
                }
                else
                {
                    noteTL.add(new Pair(nowNoteL, 0));
                }

                if (noteTH.size() > 0)
                {
                    if (noteTH.get(noteTH.size() - 1).x == nowNoteH && noteTH.get(noteTH.size() - 1).y < allDuration.length() - 1)
                    {
                        noteTH.set(noteTH.size() - 1, new Pair(nowNoteH, noteTH.get(noteTH.size() - 1).y + 1));
                    }
                    else
                    {
                        noteTH.add(new Pair(nowNoteH, 0));
                    }
                }
                else
                {
                    noteTH.add(new Pair(nowNoteH, 0));
                }

                trackL += " " + pop.get(Math.min(sizP - 1, (minY / (iHight / sizP)))) + "s";
                trackH += " " + pop.get(Math.min(sizP - 1, (maxY / (iHight / sizP)))) + "s";
            }

            //            StringBuilder trackLB = new StringBuilder(trackL);
            //            trackLB.setCharAt(trackL.length() - 1, 'w');
            //            trackL = trackLB.toString();
            //
            //            StringBuilder trackHB = new StringBuilder(trackH);
            //            trackHB.setCharAt(trackH.length() - 1, 'w');
            //            trackH = trackHB.toString();

            if (noteTL.size() > 0)
                noteTL.set(noteTL.size() - 1, new Pair(noteTL.get(noteTL.size() - 1).x, allDuration.length() - 1));
            if (noteTH.size() > 0)
                noteTH.set(noteTH.size() - 1, new Pair(noteTH.get(noteTH.size() - 1).x, allDuration.length() - 1));

            String tmpL = "", tmpH = "";
            for (Pair pr:noteTL)
                tmpL += " " + pop.get(pr.x) + allDuration.charAt(pr.y);
            for (Pair pr:noteTH)
                tmpH += " " + pop.get(pr.x) + allDuration.charAt(pr.y);


            //allTrack.add(trackH);
            //allTrack.add(trackL);

            allTrack.add(tmpH);
            allTrack.add(tmpL);
        }

        int totalSiz = 0;
        for (String str:allTrack) totalSiz += str.length();
        final double percent = 1e-4;

        ArrayList<String> newAllTrack = new ArrayList<String>();
        for (String str:allTrack)
        {
            if ((double)str.length() >= (double)totalSiz * percent) newAllTrack.add(str);
        }

        List<String> wavFileList=new ArrayList<>();
        int step = 2;
        for (int i = 0, siz = newAllTrack.size(); i <= siz - 1; i += step)
        {
            Player pl = new Player();
            Pattern tmp = new Pattern();

            ArrayList<String> nowList = new ArrayList<String>();

            for (int j = 0; j <= step - 1; ++j)
            {
                if (i + j >= siz) break;
                nowList.add(newAllTrack.get(i + j));
            }

            int maxTrackL = 0;
            for (String str:nowList)
            {
                maxTrackL = Math.max(maxTrackL, str.length());
            }
            for (int k = 0; k <= nowList.size() - 1; ++k)
            {
                int orgSiz = nowList.get(k).length();
                String tmpS = nowList.get(k);
                for (int j = 0; tmpS.length() <= maxTrackL - 1; j = (j + 1) % orgSiz)
                {
                    tmpS += tmpS.charAt(j);
                }

                StringBuilder tmpSB = new StringBuilder(tmpS);
                tmpSB.setCharAt(tmpS.length() - 1, 'o');
                tmpS = tmpSB.toString();

                nowList.set(k, tmpS);
            }

            for (int j = 0; j <= nowList.size() - 1; ++j)
            {
                String sound;
                sound = "V" + j + nowList.get(j);
                tmp.add(sound);
                System.out.println(sound);
            }

            tmp.setTempo(180 );
            tmp.setInstrument(0);

            //pl.play(tmp);

            //输出到midi文件并转成wav文件
            String midiFileName=file.getAbsolutePath().replaceAll("[.][^.]+$", "") + "-" + i+".midi";
            File midiFile = new File(midiFileName);
            MidiFileManager.savePatternToMidi(tmp, midiFile);
            String wavF=MidiConvertToWav(midiFileName);
            wavFileList.add(wavF);
        }

        //合并wav文件
        if(wavFileList.isEmpty()==false)
            MergeToWav(wavFileList,file.getAbsolutePath());

    }


    private void MergeToWav(List<String> wavFileList,String imageFileName )
    {
        try
        {
            System.out.println("total:"+wavFileList.size());
            String wavFileName = imageFileName.substring(0,imageFileName.lastIndexOf('.')+1)+"wav";
            File fileOut = new File(wavFileName);

            AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(wavFileList.get(0)));
            if (wavFileList.size() >= 2)
            {
                //AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(midiFileList.get(0)));
                AudioInputStream audio2 = AudioSystem.getAudioInputStream(new File(wavFileList.get(1)));
                AudioInputStream audioBuild = new AudioInputStream(new SequenceInputStream(audio1, audio2), audio1.getFormat(), audio1.getFrameLength() + audio2.getFrameLength());

                System.out.println("0->1");

                //大于两个时继续合并
                for (int i = 2; i < wavFileList.size(); i++)
                {
                    System.out.println((i-1) + "->" +i);
                    AudioInputStream audio3 = AudioSystem.getAudioInputStream(new File(wavFileList.get(i)));
                    audioBuild = new AudioInputStream(new SequenceInputStream(audioBuild, audio3), audioBuild.getFormat(), audioBuild.getFrameLength() + audio3.getFrameLength());
                    //audio3.close();
                }
                //生成语音
                AudioSystem.write(audioBuild, AudioFileFormat.Type.WAVE, fileOut);

                audio2.close();
                audioBuild.close();

            }
            else
            {
                AudioSystem.write(audio1, AudioFileFormat.Type.WAVE, fileOut);
            }
            audio1.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private String MidiConvertToWav(String midiFileName)
    {
        try{
            System.out.println("转换中..."+midiFileName);

            String wavFileName = midiFileName.substring(0,midiFileName.lastIndexOf('.')+1)+"wav";

            //获取音频输入流
            AudioInputStream  audioStream = AudioSystem.getAudioInputStream(new File(midiFileName));
            try {
                var file=new File(wavFileName);
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
                audioStream.close();
                return file.getAbsolutePath();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}


