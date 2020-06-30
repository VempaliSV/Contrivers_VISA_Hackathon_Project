SQLALCHEMY_DATABASE_URI = "Enter Database URI"
SQLALCHEMY_TRACK_MODIFICATIONS = False
PROPAGATE_EXCEPTIONS = True
JWT_BLACKLIST_ENABLED = True  # enable blacklist feature
JWT_BLACKLIST_TOKEN_CHECK = ["access", "refresh"]  # allowing blacklisting for access and refresh tokens
