package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream().map(elem -> elem.songName).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.entrySet()
                     .stream()
                     .filter(elem -> elem.getValue().intValue() == year)
                     .map(elem -> elem.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) songs.stream()
                    .filter(s -> s.albumName.isPresent() && s.albumName.get().equals(albumName))
                    .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) songs.stream()
                    .filter(s -> s.albumName.isEmpty())
                    .count();
    }
    
    private OptionalDouble totalDurationOfAlbum(final String albumName) {
        return OptionalDouble.of(songsInAlbum(albumName).mapToDouble(e -> e.duration).sum());
    }
    
    private Stream<Song> songsInAlbum(final String albumName) {
        return songs.stream().filter(s -> s.albumName.isPresent() && s.albumName.get().equals(albumName));
    }
    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
         return songs.stream()
                 .filter(elem -> elem.albumName.isPresent() && elem.albumName.get().equals(albumName))
                 .mapToDouble(s -> s.duration).average();
    }

    @Override
    public Optional<String> longestSong() {
        return songs.stream()
                    .max((s1, s2) -> Double.compare(s1.duration, s2.duration)).map(e -> e.songName);
    }

    @Override
    public Optional<String> longestAlbum() {
        return albums.entrySet().stream()
                                .max((e1, e2) -> Double.compare(totalDurationOfAlbum(e1.getKey()).orElse(0.0), 
                                                                totalDurationOfAlbum(e2.getKey()).orElse(0.0)))
                                .map(Map.Entry::getKey);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
