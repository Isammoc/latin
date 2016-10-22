# Latin

Application pour afficher correctement une analyse grammaticale d'un texte en latin.

## Backups

### Create backup

```
heroku pg:backups capture
curl -o ../backup/latin/db/`date +%Y%m%d`.dump `heroku pg:backups public-url`
```

