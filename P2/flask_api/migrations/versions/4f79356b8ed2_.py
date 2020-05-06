"""empty message

Revision ID: 4f79356b8ed2
Revises: dd8e876b49ea
Create Date: 2020-04-30 12:36:09.299915

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '4f79356b8ed2'
down_revision = 'dd8e876b49ea'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('accounts',
    sa.Column('username', sa.String(length=30), nullable=False),
    sa.Column('password', sa.String(), nullable=False),
    sa.Column('is_admin', sa.Integer(), nullable=False),
    sa.Column('available_money', sa.Integer(), nullable=True),
    sa.PrimaryKeyConstraint('username'),
    sa.UniqueConstraint('username')
    )
    op.create_table('orders',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('username', sa.String(length=30), nullable=False),
    sa.Column('id_event', sa.Integer(), nullable=False),
    sa.Column('tickets_bought', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['username'], ['accounts.username'], ),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('orders')
    op.drop_table('accounts')
    # ### end Alembic commands ###